package com.fulfillx.order.service;

import com.fulfillx.order.domain.dto.CreateOrderRequest;
import com.fulfillx.order.domain.dto.OrderResponse;
import com.fulfillx.order.events.messaging.InventoryReservationResultEvent;
import com.fulfillx.order.events.saga.inventory.InventoryReleaseResultEvent;
import com.fulfillx.order.events.saga.payment.PaymentProcessingResultEvent;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest orderRequest);
    void handleInventoryReservationResult(InventoryReservationResultEvent event);
    void handlePaymentProcessingResult(PaymentProcessingResultEvent event);
    void handleInventoryReleaseResult(InventoryReleaseResultEvent event);
}
