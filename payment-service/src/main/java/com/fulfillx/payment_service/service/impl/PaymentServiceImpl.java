package com.fulfillx.payment_service.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fulfillx.payment_service.domain.model.Money;
import com.fulfillx.payment_service.domain.model.OutboxEvent;
import com.fulfillx.payment_service.domain.model.Payment;
import com.fulfillx.payment_service.dto.CreatePaymentRequest;
import com.fulfillx.payment_service.dto.PaymentResponse;
import com.fulfillx.payment_service.dto.PaymentSimulationResult;
import com.fulfillx.payment_service.events.saga.PaymentProcessingResultEvent;
import com.fulfillx.payment_service.events.saga.ProcessPaymentCommand;
import com.fulfillx.payment_service.mapper.PaymentMapper;
import com.fulfillx.payment_service.repository.OutboxEventRepository;
import com.fulfillx.payment_service.repository.PaymentRepository;
import com.fulfillx.payment_service.service.PaymentService;
import com.fulfillx.payment_service.service.PaymentSimulator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final OutboxEventRepository eventRepository;
    private final ObjectMapper objectMapper;
    private final PaymentSimulator paymentSimulator;

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

    @Override
    @Transactional
    public void processPayment(ProcessPaymentCommand command) {
        Objects.requireNonNull(command, "Payment command must not be null");
        String idempotencyKey = "order-" + command.orderId() + "-saga-" + command.sagaId();
        var existingPayment = paymentRepository.findByIdempotencyKey(idempotencyKey);

        if (existingPayment.isPresent()) {
            Payment payment = existingPayment.get();

            boolean sameOrder = payment.getOrderId().equals(command.orderId());
            boolean sameAmount = payment.getMoney().getAmount().compareTo(command.amount()) == 0;
            boolean sameCurrency = payment.getMoney().getCurrency().equals(command.currency());

            if (!sameOrder || !sameAmount || !sameCurrency) {
                throw new IllegalStateException("Idempotency key has already been used with different payment details");
            }
            boolean resultEventExists = eventRepository.existsByAggregateTypeAndAggregateIdAndEventType(
                    "PAYMENT",
                    payment.getId(),
                    "PAYMENT_PROCESSED"
            );
            if (resultEventExists) {
                log.info("Payment already processed: orderId = {}, paymentId = {}",
                        payment.getOrderId(), payment.getId());
                return;
            }
            throw new IllegalStateException("Payment exists but event is missing. orderId = " + payment.getOrderId());
        }
        Money money = new Money(
                command.amount(),
                command.currency()
        );
        Payment payment = Payment.create(
                command.orderId(),
                money,
                idempotencyKey
        );
        payment.startProcessing();
        PaymentSimulationResult simulatorResult = paymentSimulator.process(money);
        if (simulatorResult.successful()) {
            payment.markSucceeded(simulatorResult.provider(), simulatorResult.providerPaymentId());
        } else {
            payment.markFailed();
        }
        paymentRepository.save(payment);
        PaymentProcessingResultEvent resultEvent =
                new PaymentProcessingResultEvent(
                        UUID.randomUUID(),
                        command.sagaId(),
                        command.orderId(),
                        simulatorResult.successful(),
                        simulatorResult.failureReason(),
                        Instant.now()
                );

        try {
            String payload = objectMapper.writeValueAsString(resultEvent);

            OutboxEvent outboxEvent = new OutboxEvent(
                    resultEvent.eventId(),
                    "PAYMENT",
                    payment.getId(),
                    "PAYMENT_PROCESSED",
                    payload
            );

            eventRepository.save(outboxEvent);

        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Could not serialize payment result event", e);
        }
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