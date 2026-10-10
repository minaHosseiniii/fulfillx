package com.fulfillx.inventory_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InventoryRequest {

    @NotNull
    @Positive
    private Long productId;

    @NotBlank
    @Size(max = 100)
    private String sku;

    @NotNull
    @Min(0)
    private Integer initialQuantity;
}