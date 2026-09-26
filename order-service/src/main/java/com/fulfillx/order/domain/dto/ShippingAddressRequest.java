package com.fulfillx.order.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record ShippingAddressRequest(
        @NotBlank
        String recipientName,

        @NotBlank
        String phoneNumber,

        @NotBlank
        String province,

        @NotBlank
        String city,

        @NotBlank
        String addressLine,

        @NotBlank
        String postalCode
) {
}
