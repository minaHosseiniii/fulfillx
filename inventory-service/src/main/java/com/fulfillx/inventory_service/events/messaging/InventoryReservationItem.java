package com.fulfillx.inventory_service.events.messaging;

public record InventoryReservationItem(
        Long productId,
        String sku,
        Integer quantity
) {
}
