package com.fulfillx.order.events.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fulfillx.order.events.messaging.InventoryReservationResultEvent;
import com.fulfillx.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class InventoryReservationResultConsumer {
    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "inventory-reservation-results",
            groupId = "order-service"
    )
    public void consume(String message) throws JsonProcessingException {
        InventoryReservationResultEvent event =
                objectMapper.readValue(message, InventoryReservationResultEvent.class);

        log.info("Received inventory reservation result: {}", event);
        orderService.handleInventoryReservationResult(event);
    }
}
