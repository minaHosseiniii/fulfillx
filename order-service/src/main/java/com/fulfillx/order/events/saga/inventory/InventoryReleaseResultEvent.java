package com.fulfillx.order.events.saga.inventory;

import java.time.Instant;
import java.util.UUID;

public record InventoryReleaseResultEvent(
        UUID eventId,
        UUID sagaId,
        Long orderId,
        Instant occurredAt,
        boolean success,
        String reason
) {}