package com.fulfillx.order.domain.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long productId,
        String productName,
        String productSku,
        BigDecimal unitPrice,
        String currency,
        Integer quantity,
        BigDecimal subTotal
) {
}
