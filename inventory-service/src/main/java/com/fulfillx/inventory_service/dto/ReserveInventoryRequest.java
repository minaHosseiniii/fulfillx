package com.fulfillx.inventory_service.dto;

import java.util.List;
import java.util.UUID;

public record ReserveInventoryRequest(
        UUID sagaId,
        Long orderId,
        List<ReservationItemRequest> items
) {
}
