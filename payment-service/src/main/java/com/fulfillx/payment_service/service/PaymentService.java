package com.fulfillx.payment_service.service;

import com.fulfillx.payment_service.dto.CreatePaymentRequest;
import com.fulfillx.payment_service.dto.PaymentResponse;
import com.fulfillx.payment_service.events.saga.ProcessPaymentCommand;

public interface PaymentService {

    PaymentResponse createPayment(CreatePaymentRequest request);

    PaymentResponse getPaymentById(Long paymentId);

    PaymentResponse getPaymentByIdempotencyKey(String idempotencyKey);

    PaymentResponse cancelPayment(Long paymentId);

    void processPayment(ProcessPaymentCommand command);
}