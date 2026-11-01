package com.fulfillx.inventory_service.events.messaging;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ReserveInventoryCommand(
        UUID eventId,
        UUID sagaId,
        Long orderId,
        Instant occurredAt,
        List<InventoryReservationItem> items
) {
}
