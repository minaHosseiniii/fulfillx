package com.fulfillx.order.service;

import com.fulfillx.order.domain.dto.CreateOrderRequest;
import com.fulfillx.order.domain.dto.OrderResponse;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest orderRequest);
}
