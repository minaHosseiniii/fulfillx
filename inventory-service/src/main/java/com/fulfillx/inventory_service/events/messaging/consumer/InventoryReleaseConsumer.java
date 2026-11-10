package com.fulfillx.inventory_service.events.messaging.consumer;

import com.fulfillx.inventory_service.events.messaging.ReleaseInventoryCommand;
import com.fulfillx.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
@Slf4j
@RequiredArgsConstructor
public class InventoryReleaseConsumer {
    private final InventoryService inventoryService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "inventory-release-commands",
            groupId = "inventory-service"
    )
    public void consume(String message) {

        ReleaseInventoryCommand command;

        try {
            command = objectMapper.readValue(
                    message,
                    ReleaseInventoryCommand.class
            );
        } catch (JacksonException exception) {
            throw new IllegalArgumentException(
                    "Could not deserialize inventory release command",
                    exception
            );
        }

        inventoryService.releaseOrder(command.orderId(), command.sagaId());
    }
}
