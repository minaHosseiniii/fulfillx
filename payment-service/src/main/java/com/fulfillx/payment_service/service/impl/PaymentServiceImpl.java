package com.fulfillx.payment_service.service.impl;

import com.fulfillx.payment_service.domain.model.Money;
import com.fulfillx.payment_service.domain.model.Payment;
import com.fulfillx.payment_service.dto.CreatePaymentRequest;
import com.fulfillx.payment_service.dto.PaymentResponse;
import com.fulfillx.payment_service.mapper.PaymentMapper;
import com.fulfillx.payment_service.repository.PaymentRepository;

import com.fulfillx.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public PaymentResponse createPayment(CreatePaymentRequest request) {

        Objects.requireNonNull(request, "Payment request must not be null");

        var existingPayment = paymentRepository.findByIdempotencyKey(
                request.getIdempotencyKey()
        );

        if (existingPayment.isPresent()) {
            Payment payment = existingPayment.get();

            validateIdempotentRequest(payment, request);

            return paymentMapper.toPaymentResponse(payment);
        }

        Money money = new Money(
                request.getAmount(),
                request.getCurrency()
        );

        Payment payment = Payment.create(
                request.getOrderId(),
                money,
                request.getIdempotencyKey()
        );

        Payment savedPayment = paymentRepository.save(payment);

        return paymentMapper.toPaymentResponse(savedPayment);
    }

    @Override
    public PaymentResponse getPaymentById(Long paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Payment not found with ID: " + paymentId
                ));

        return paymentMapper.toPaymentResponse(payment);
    }

    @Override
    public PaymentResponse getPaymentByIdempotencyKey(
            String idempotencyKey
    ) {

        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException(
                    "Idempotency key must not be blank"
            );
        }

        Payment payment = paymentRepository
                .findByIdempotencyKey(idempotencyKey)
                .orElseThrow(() -> new NoSuchElementException(
                        "Payment not found for the given idempotency key"
                ));

        return paymentMapper.toPaymentResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponse cancelPayment(Long paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Payment not found with ID: " + paymentId
                ));

        payment.cancel();

        Payment savedPayment = paymentRepository.save(payment);

        return paymentMapper.toPaymentResponse(savedPayment);
    }

    private void validateIdempotentRequest(
            Payment existingPayment,
            CreatePaymentRequest request
    ) {

        boolean sameOrder = existingPayment.getOrderId()
                .equals(request.getOrderId());

        boolean sameAmount = existingPayment.getMoney()
                .getAmount()
                .compareTo(request.getAmount()) == 0;

        boolean sameCurrency = existingPayment.getMoney()
                .getCurrency()
                .equals(request.getCurrency());

        if (!sameOrder || !sameAmount || !sameCurrency) {
            throw new IllegalArgumentException(
                    "Idempotency key has already been used "
                            + "with different payment details"
            );
        }
    }
}