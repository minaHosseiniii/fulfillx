package com.fulfillx.delivery_service.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class DeliveryAddress {

    @Column(name = "recipient_name", nullable = false, length = 150)
    private String recipientName;

    @Column(name = "phone_number", nullable = false, length = 30)
    private String phoneNumber;

    @Column(length = 100)
    private String province;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(name = "address_line", nullable = false, length = 500)
    private String addressLine;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    public DeliveryAddress(
            String recipientName,
            String phoneNumber,
            String province,
            String city,
            String addressLine,
            String postalCode) {

        this.recipientName = requireText(recipientName, "Recipient name");
        this.phoneNumber = requireText(phoneNumber, "Phone number");
        this.province = province;
        this.city = requireText(city, "City");
        this.addressLine = requireText(addressLine, "Address line");
        this.postalCode = postalCode;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be blank");
        }

        return value.trim();
    }
}
