package com.fulfillx.order.domain.dto;

import com.fulfillx.order.domain.model.enums.OrderStatus;

import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        Long customerId,
        OrderStatus status,
        ShippingAddressResponse shippingAddress,
        MoneyResponse totalAmount,
        List<OrderItemResponse> items,
        Instant createdAt
) {
}
