package com.fulfillx.order.events.saga.inventory;

import java.time.Instant;
import java.util.UUID;

public record ReleaseInventoryCommand(
        UUID eventId,
        UUID sagaId,
        Long orderId,
        Instant occurredAt
) {
}
