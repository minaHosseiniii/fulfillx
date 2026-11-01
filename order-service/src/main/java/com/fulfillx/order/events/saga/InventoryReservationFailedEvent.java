package com.fulfillx.order.events.saga;

import java.time.Instant;
import java.util.UUID;

public record InventoryReservationFailedEvent(
        UUID eventId,
        UUID sagaId,
        Long orderId,
        Instant occurredAt,
        String reason
) {}