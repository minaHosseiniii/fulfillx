package com.fulfillx.order.domain.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long productId,
        String productName,
        BigDecimal unitPrice,
        String currency,
        Integer quantity,
        BigDecimal subTotal
) {
}
