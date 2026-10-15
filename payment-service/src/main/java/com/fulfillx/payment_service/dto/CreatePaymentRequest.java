package com.fulfillx.payment_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class CreatePaymentRequest {

    @NotNull(message = "Order ID must not be null")
    @Positive(message = "Order ID must be positive")
    private Long orderId;

    @NotNull(message = "Amount must not be null")
    @DecimalMin(
            value = "0.01",
            message = "Payment amount must be greater than zero"
    )
    @Digits(
            integer = 17,
            fraction = 2,
            message = "Amount must have at most 17 integer digits and 2 decimal places"
    )
    private BigDecimal amount;

    @NotBlank(message = "Currency must not be blank")
    @Pattern(
            regexp = "[A-Z]{3}",
            message = "Currency must contain three uppercase letters"
    )
    private String currency;

    @NotBlank(message = "Idempotency key must not be blank")
    @Size(
            max = 100,
            message = "Idempotency key must not exceed 100 characters"
    )
    private String idempotencyKey;
}