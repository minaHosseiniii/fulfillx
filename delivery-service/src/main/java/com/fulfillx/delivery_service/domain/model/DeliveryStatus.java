package com.fulfillx.delivery_service.domain.model;

import lombok.Getter;

@Getter
public enum DeliveryStatus {
    PENDING,
    PREPARING,
    READY_FOR_PICKUP,
    IN_TRANSIT,
    DELIVERED,
    FAILED,
    RETURNED,
    CANCELLED
}
