package com.fulfillx.order.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID eventId,
        String eventType,
        Instant occurredAt,
        Long orderId,
        Long customerId,
        BigDecimal totalAmount,
        String currency
) {
}
