package com.fulfillx.order.events.saga;

public record InventoryReservationItem (
        Long productId,
        String sku,
        Integer quantity
){
}
