package com.fulfillx.inventory_service.events.messaging;

import com.fulfillx.inventory_service.domain.model.OutboxEvent;
import com.fulfillx.inventory_service.domain.model.OutboxStatus;
import com.fulfillx.inventory_service.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryOutboxPublisher {

    private final OutboxEventRepository outboxRepository;

    @Qualifier("outboxKafkaTemplate")
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 5000)
    public void publishPendingEvents() {

        List<OutboxEvent> events =
                outboxRepository.findTop100ByStatusOrderByCreatedAtAsc(
                        OutboxStatus.PENDING
                );

        for (OutboxEvent event : events) {
            try {
                String topic = resolveTopic(event.getEventType());
                kafkaTemplate.send(
                        topic,
                        event.getAggregateId().toString(),
                        event.getPayload()
                ).get();

                event.markPublished();
                outboxRepository.save(event);

            } catch (Exception exception) {
                log.error(
                        "Failed to publish outbox event: {}",
                        event.getId(),
                        exception
                );
            }
        }
    }

    private String resolveTopic(String eventType) {
        return switch (eventType) {
            case "INVENTORY_RESERVATION_RESULT" ->
                    "inventory-reservation-results";

            case "INVENTORY_RELEASE_RESULT" ->
                    "inventory-release-results";

            default -> throw new IllegalArgumentException("Unknown event type: " + eventType);
        };
    }
}
