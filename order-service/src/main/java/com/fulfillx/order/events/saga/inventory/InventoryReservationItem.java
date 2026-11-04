package com.fulfillx.order.events.saga.inventory;

public record InventoryReservationItem (
        Long productId,
        Integer quantity
){
}
