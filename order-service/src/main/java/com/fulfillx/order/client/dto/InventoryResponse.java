package com.fulfillx.order.client.dto;

public record InventoryResponse(
        Long id,
        Long productId,
        String sku,
        int availableQuantity,
        int reservedQuantity
) {
}
