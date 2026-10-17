package com.fulfillx.delivery_service.dto;

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
public class CreateDeliveryRequest {

    @NotNull
    @Positive
    private Long orderId;

    @NotBlank
    @Size(max = 150)
    private String recipientName;

    @NotBlank
    @Size(max = 30)
    private String phoneNumber;

    @Size(max = 100)
    private String province;

    @NotBlank
    @Size(max = 100)
    private String city;

    @NotBlank
    @Size(max = 500)
    private String addressLine;

    @Size(max = 20)
    private String postalCode;
}
