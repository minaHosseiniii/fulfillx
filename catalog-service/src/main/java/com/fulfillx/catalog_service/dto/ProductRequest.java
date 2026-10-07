package com.fulfillx.catalog_service.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductRequest(

        @NotBlank
        @Size(max = 150)
        String name,

        @Size(max = 500)
        String description,

        @NotBlank
        @Size(max = 100)
        String sku,

        @NotNull
        @DecimalMin(value = "0.0", inclusive = false)
        @Digits(integer = 17, fraction = 2)
        BigDecimal price,

        @NotBlank
        @Size(min = 3, max = 3)
        String currency
) {
}
