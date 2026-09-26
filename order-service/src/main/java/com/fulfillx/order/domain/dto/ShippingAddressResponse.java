package com.fulfillx.order.domain.dto;

public record ShippingAddressResponse(
        String recipientName,
        String phoneNumber,
        String province,
        String city,
        String addressLine,
        String postalCode
) {
}
