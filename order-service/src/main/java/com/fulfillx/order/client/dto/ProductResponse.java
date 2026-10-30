package com.fulfillx.order.client.dto;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        String sku,
        BigDecimal price,
        String currency,
        String status
) {
}
