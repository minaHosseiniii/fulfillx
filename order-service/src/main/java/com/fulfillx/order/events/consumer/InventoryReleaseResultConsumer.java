package com.fulfillx.order.events.consumer;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import com.fulfillx.order.events.saga.inventory.InventoryReleaseResultEvent;
import com.fulfillx.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class InventoryReleaseResultConsumer {
    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "inventory-release-results",
            groupId = "order-service"
    )
    public void consume(String message) throws JacksonException {
        log.info("Received inventory release result event {}", message);
        InventoryReleaseResultEvent event = objectMapper.readValue(message, InventoryReleaseResultEvent.class);
        orderService.handleInventoryReleaseResult(event);
    }
}
