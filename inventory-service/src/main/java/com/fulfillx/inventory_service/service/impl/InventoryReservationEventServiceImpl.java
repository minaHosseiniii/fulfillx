package com.fulfillx.inventory_service.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fulfillx.inventory_service.domain.model.OutboxEvent;
import com.fulfillx.inventory_service.events.messaging.InventoryReservationResultEvent;
import com.fulfillx.inventory_service.repository.OutboxEventRepository;
import com.fulfillx.inventory_service.service.InventoryReservationEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryReservationEventServiceImpl implements InventoryReservationEventService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Transactional(propagation = Propagation.MANDATORY)
    public void saveResult(InventoryReservationResultEvent event) {

        try {
            String payload = objectMapper.writeValueAsString(event);

            OutboxEvent outboxEvent = new OutboxEvent(
                    event.eventId(),
                    "INVENTORY_RESERVATION",
                    event.orderId(),
                    event.sagaId(),
                    "INVENTORY_RESERVATION_RESULT",
                    payload
            );

            outboxEventRepository.save(outboxEvent);

        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Could not serialize inventory reservation result", e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveFailureResult(InventoryReservationResultEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            OutboxEvent outboxEvent = new OutboxEvent(
                    event.eventId(),
                    "INVENTORY_RESERVATION",
                    event.orderId(),
                    event.sagaId(),
                    "INVENTORY_RESERVATION_RESULT",
                    payload
            );

            outboxEventRepository.save(outboxEvent);

        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Could not serialize inventory reservation result", e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void ensureSuccessResultExists(UUID sagaId, Long orderId) {

        boolean exists =
                outboxEventRepository
                        .findByAggregateIdAndAggregateTypeAndSagaIdAndEventType(
                                orderId,
                                "INVENTORY_RESERVATION",
                                sagaId,
                                "INVENTORY_RESERVATION_RESULT"
                        )
                        .isPresent();

        if (exists) {
            return;
        }

        InventoryReservationResultEvent event =
                new InventoryReservationResultEvent(
                        UUID.randomUUID(),
                        sagaId,
                        orderId,
                        Instant.now(),
                        true,
                        null
                );

        saveResult(event);
    }
}