package com.fulfillx.order.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateOrderItemRequest(

        @Schema(
                description = "Product identifier",
                example = "100"
        )
        @NotNull
                Long productId,

        @Schema(
                description = "Product name",
                example = "Lipstick"
        )
        @NotBlank
        String productName,

        @Schema(
                description = "Unit price",
                example = "500000"
        )
        @NotNull
        @Positive
        BigDecimal unitPrice,

        @Schema(
                description = "Number of items",
                example = "2"
        )
        @NotNull
        @Positive
        Integer quantity
) {
}
