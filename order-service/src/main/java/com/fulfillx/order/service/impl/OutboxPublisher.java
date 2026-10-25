package com.fulfillx.order.service.impl;

import com.fulfillx.order.domain.model.OutboxEvent;
import com.fulfillx.order.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${fulfillx.kafka.topics.notification-events:notification-events}")
    private String notificationEventsTopic;

    @Scheduled(fixedDelayString = "${fulfillx.outbox.poll-interval-ms:1000}")
    public void publishPendingEvents() {
        List<OutboxEvent> events = findPendingEvents();

        for (OutboxEvent event : events) {
            try {
                kafkaTemplate.send(
                        notificationEventsTopic,
                        String.valueOf(event.getAggregateId()),
                        event.getPayload()
                ).get(5, TimeUnit.SECONDS);

                markPublished(event);

                log.info(
                        "Published outbox event id={}, type={}, aggregateId={}",
                        event.getId(),
                        event.getEventType(),
                        event.getAggregateId()
                );

            } catch (Exception exception) {
                recordFailure(event, exception);

                log.error(
                        "Failed to publish outbox event id={}",
                        event.getId(),
                        exception
                );
            }
        }
    }

    @Transactional(readOnly = true)
    public List<OutboxEvent> findPendingEvents() {
        return outboxEventRepository
                .findByPublishedAtIsNullOrderByCreatedAtAsc(
                        PageRequest.of(0, 50)
                );
    }

    @Transactional
    public void markPublished(OutboxEvent event) {
        event.markPublished();
        outboxEventRepository.save(event);
    }

    @Transactional
    public void recordFailure(
            OutboxEvent event,
            Exception exception
    ) {
        String message = exception.getMessage();

        event.recordFailure(
                message == null
                        ? exception.getClass().getSimpleName()
                        : message
        );

        outboxEventRepository.save(event);
    }
}