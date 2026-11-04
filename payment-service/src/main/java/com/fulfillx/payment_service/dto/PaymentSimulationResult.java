package com.fulfillx.payment_service.dto;

public record PaymentSimulationResult(
        boolean successful,
        String failureReason,
        String provider,
        String providerPaymentId
) {
}
