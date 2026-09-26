package com.fulfillx.order.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateOrderRequest(
        @Schema(
                description = "Customer identifier",
                example = "1"
        )
        @NotNull
        Long customerId,

        @NotNull
        @Valid
        ShippingAddressRequest shippingAddress,

        @Schema(
                description = "Order currency",
                example = "IRR"
        )
        @NotBlank
        String currency,

        @NotEmpty
        @Valid
        List<CreateOrderItemRequest> items
) {
}
