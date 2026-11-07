package com.fulfillx.inventory_service.events.messaging;

import java.time.Instant;
import java.util.UUID;

public record ReleaseInventoryCommand(
        UUID eventId,
        UUID sagaId,
        Long orderId,
        Instant occurredAt
) {
}
