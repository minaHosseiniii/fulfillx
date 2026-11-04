package com.fulfillx.payment_service.service.impl;

import com.fulfillx.payment_service.domain.model.OutboxEvent;
import com.fulfillx.payment_service.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentOutboxPublisher {
    private final OutboxEventRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 5000)
    public void publishPendingEvents() {
        List<OutboxEvent> pendingEvents = repository.findByPublishedFalseOrderByOccurredAtAsc();
        for (OutboxEvent outboxEvent : pendingEvents) {
            try {
                String topic = resolveTopic(outboxEvent.getEventType());
                kafkaTemplate.send(
                        topic,
                        outboxEvent.getAggregateId().toString(),
                        outboxEvent.getPayload()
                ).get();

                outboxEvent.markAsPublished();
                repository.save(outboxEvent);

                log.info("Outbox event published: id = {}, aggregateId = {}, type = {}, topic = {}",
                        outboxEvent.getId(),
                        outboxEvent.getAggregateId(),
                        outboxEvent.getEventType(),
                        topic);

            } catch (Exception e) {
                log.error("Failed to publish outbox event: id = {}, type = {}",
                        outboxEvent.getId(), outboxEvent.getEventType(), e);
            }
        }
    }

    private String resolveTopic(String eventType) {
        return switch (eventType) {
            case "PAYMENT_PROCESSED" -> "payment-processing-results";
            default -> throw new IllegalStateException("Unknown outbox event type: " + eventType);
        };
    }
}
