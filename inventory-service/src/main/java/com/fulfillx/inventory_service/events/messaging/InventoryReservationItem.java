package com.fulfillx.inventory_service.events.messaging;

public record InventoryReservationItem(
        Long productId,
        Integer quantity
) {
}
