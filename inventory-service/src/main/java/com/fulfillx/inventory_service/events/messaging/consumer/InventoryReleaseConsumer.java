package com.fulfillx.inventory_service.events.messaging.consumer;

import com.fulfillx.inventory_service.events.messaging.ReleaseInventoryCommand;
import com.fulfillx.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class InventoryReleaseConsumer {
    private final InventoryService inventoryService;

    @KafkaListener(
            topics = "inventory-release-commands",
            groupId = "inventory-service"
    )
    public void consume(ReleaseInventoryCommand command) {
        log.info("Received release command: {}", command);
        inventoryService.releaseOrder(command.orderId(), command.sagaId());
    }
}
