package com.fulfillx.payment_service.events.saga;

import java.time.Instant;
import java.util.UUID;

public record PaymentProcessingResultEvent(
        UUID eventId,
        UUID sagaId,
        Long orderId,
        boolean successful,
        String failureReason,
        Instant occurredAt
) {
}
