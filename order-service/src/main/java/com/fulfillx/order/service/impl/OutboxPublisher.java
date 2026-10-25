
package com.fulfillx.order.service.impl;

import com.fulfillx.order.domain.model.OutboxEvent;
import com.fulfillx.order.service.OutboxEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisher {

    private final OutboxEventService outboxEventService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${fulfillx.kafka.topics.notification-events:notification-events}")
    private String notificationEventsTopic;

    @Scheduled(fixedDelayString = "${fulfillx.outbox.poll-interval-ms:1000}")
    public void publishPendingEvents() {
        List<OutboxEvent> events = outboxEventService.findPendingEvents();

        for (OutboxEvent event : events) {
            try {
                kafkaTemplate.send(
                        notificationEventsTopic,
                        String.valueOf(event.getAggregateId()),
                        event.getPayload()
                ).get(5, TimeUnit.SECONDS);

                outboxEventService.markPublished(event.getId());

                log.info(
                        "Published outbox event id={}, type={}, aggregateId={}",
                        event.getId(),
                        event.getEventType(),
                        event.getAggregateId()
                );

            } catch (Exception exception) {
                String errorMessage = exception.getMessage() != null
                        ? exception.getMessage()
                        : exception.getClass().getSimpleName();

                try {
                    outboxEventService.recordFailure(
                            event.getId(),
                            errorMessage
                    );
                } catch (Exception persistenceException) {
                    log.error(
                            "Could not record failure for outbox event id={}",
                            event.getId(),
                            persistenceException
                    );
                }

                log.error(
                        "Failed to publish outbox event id={}",
                        event.getId(),
                        exception
                );
            }
        }
    }
}
