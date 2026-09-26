package com.fulfillx.order.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateOrderItemRequest(
        @NotNull
        Long productId,

        @NotBlank
        String productName,

        @NotNull
        @Positive
        BigDecimal unitPrice,

        @NotNull
        @Positive
        Integer quantity
) {
}
