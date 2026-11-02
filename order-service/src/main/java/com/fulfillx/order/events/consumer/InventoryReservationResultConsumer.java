package com.fulfillx.order.events.consumer;

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

    @KafkaListener(
            topics = "inventory-reservation-results",
            groupId = "order-service"
    )
    public void consume(InventoryReservationResultEvent event) {
        orderService.handleInventoryReservationResult(event);
    }
}
