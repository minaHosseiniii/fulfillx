package com.fulfillx.order.domain.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShippingAddress {
    private String recipientName;
    private String phoneNumber;
    private String province;
    private String city;
    private String addressLine;
    private String postalCode;
}
