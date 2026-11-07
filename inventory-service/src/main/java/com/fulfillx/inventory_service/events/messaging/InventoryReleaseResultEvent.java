package com.fulfillx.inventory_service.events.messaging;

import java.time.Instant;
import java.util.UUID;

public record InventoryReleaseResultEvent(
        UUID eventId,
        UUID sagaId,
        Long orderId,
        Instant occurredAt,
        boolean successful,
        String reason
) {
}
