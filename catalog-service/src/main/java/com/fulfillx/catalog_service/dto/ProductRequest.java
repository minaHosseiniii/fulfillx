package com.fulfillx.catalog_service.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductRequest{

        @NotBlank
        @Size(max = 150)
        private String name;

        @Size(max = 500)
        private String description;

        @NotBlank
        @Size(max = 100)
        private String sku;

        @NotNull
        @DecimalMin(value = "0.0", inclusive = false)
        @Digits(integer = 17, fraction = 2)
        private BigDecimal price;

        @NotBlank
        @Size(min = 3, max = 3)
        private String currency;

}
