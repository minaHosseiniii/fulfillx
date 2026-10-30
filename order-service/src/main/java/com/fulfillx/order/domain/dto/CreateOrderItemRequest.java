package com.fulfillx.order.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateOrderItemRequest(

        @Schema(
                description = "Product identifier",
                example = "100"
        )
        @NotNull
                Long productId,

        @Schema(
                description = "Number of items",
                example = "2"
        )
        @NotNull
        @Positive
        Integer quantity
) {
}
