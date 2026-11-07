package com.fulfillx.order.events.saga.payment;

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
