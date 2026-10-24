package com.fulfillx.notification_service.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record NotificationEvent(
        UUID eventId,
        NotificationEventType eventType,
        Instant occurredAt,
        Long orderId,
        Long customerId,
        BigDecimal totalAmount,
        String currency
) {
}
