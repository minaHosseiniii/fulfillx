package com.fulfillx.inventory_service.dto;

public record ReservationItemRequest(
        Long productId,
        Integer quantity
) {
}
