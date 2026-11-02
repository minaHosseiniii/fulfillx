package com.fulfillx.order.service;

import com.fulfillx.order.domain.dto.CreateOrderRequest;
import com.fulfillx.order.domain.dto.OrderResponse;
import com.fulfillx.order.events.messaging.InventoryReservationResultEvent;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest orderRequest);
    void handleInventoryReservationResult(InventoryReservationResultEvent event);
}
