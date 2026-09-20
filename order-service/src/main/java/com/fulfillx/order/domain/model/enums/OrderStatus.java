package com.fulfillx.order.domain.model.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING,
    INVENTORY_RESERVATION_PENDING,
    INVENTORY_RESERVED,
    PAYMENT_PENDING,
    PAYMENT_FAILED,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED

}
