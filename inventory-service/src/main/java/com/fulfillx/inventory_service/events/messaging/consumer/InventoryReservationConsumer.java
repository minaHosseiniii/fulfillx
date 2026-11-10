package com.fulfillx.inventory_service.events.messaging.consumer;


import com.fulfillx.inventory_service.events.messaging.InventoryReservationResultEvent;
import com.fulfillx.inventory_service.events.messaging.ReserveInventoryCommand;
import com.fulfillx.inventory_service.exception.InsufficientStockException;
import com.fulfillx.inventory_service.exception.InventoryNotFoundException;
import com.fulfillx.inventory_service.service.InventoryReservationEventService;
import com.fulfillx.inventory_service.service.InventoryService;
import com.fulfillx.inventory_service.dto.ReserveInventoryRequest;
import com.fulfillx.inventory_service.dto.ReservationItemRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryReservationConsumer {

    private final InventoryService inventoryService;
    private final InventoryReservationEventService reservationEventService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "inventory-reservation-commands",
            groupId = "inventory-service"
    )
    public void consume(String message) {

        ReserveInventoryCommand command;

        try {
            command = objectMapper.readValue(
                    message,
                    ReserveInventoryCommand.class
            );
        } catch (JacksonException exception) {
            throw new IllegalArgumentException(
                    "Could not deserialize inventory reservation command",
                    exception
            );
        }

        try {
            ReserveInventoryRequest request = new ReserveInventoryRequest(
                    command.sagaId(),
                    command.orderId(),
                    command.items().stream()
                            .map(item -> new ReservationItemRequest(
                                    item.productId(),
                                    item.quantity()
                            ))
                            .toList()
            );

            inventoryService.reserveOrder(request);

        } catch (InsufficientStockException | InventoryNotFoundException exception) {

            InventoryReservationResultEvent failureEvent =
                    new InventoryReservationResultEvent(
                            UUID.randomUUID(),
                            command.sagaId(),
                            command.orderId(),
                            Instant.now(),
                            false,
                            exception.getMessage()
                    );

            reservationEventService.saveFailureResult(failureEvent);
        }
    }
}