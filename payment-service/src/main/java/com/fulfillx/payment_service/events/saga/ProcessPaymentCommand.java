package com.fulfillx.payment_service.events.saga;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProcessPaymentCommand(
        UUID eventId,
        UUID sagaId,
        Long orderId,
        BigDecimal amount,
        String currency,
        Instant occurredAt
) {
}
