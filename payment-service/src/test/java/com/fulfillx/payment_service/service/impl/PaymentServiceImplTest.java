package com.fulfillx.payment_service.service.impl;

import com.fulfillx.payment_service.domain.model.Money;
import com.fulfillx.payment_service.domain.model.Payment;
import com.fulfillx.payment_service.domain.model.PaymentStatus;
import com.fulfillx.payment_service.dto.CreatePaymentRequest;
import com.fulfillx.payment_service.dto.PaymentResponse;
import com.fulfillx.payment_service.mapper.PaymentMapper;
import com.fulfillx.payment_service.repository.PaymentRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private PaymentResponse paymentResponse;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private CreatePaymentRequest request;

    @BeforeEach
    void setUp() {
        request = new CreatePaymentRequest();
        request.setOrderId(42L);
        request.setAmount(new BigDecimal("150.00"));
        request.setCurrency("USD");
        request.setIdempotencyKey("payment-key-123");
    }

    @Test
    void shouldCreatePaymentWhenRequestIsValid() {
        when(paymentRepository.findByIdempotencyKey(
                request.getIdempotencyKey()
        )).thenReturn(Optional.empty());

        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(paymentMapper.toPaymentResponse(any(Payment.class)))
                .thenReturn(paymentResponse);

        PaymentResponse result = paymentService.createPayment(request);

        ArgumentCaptor<Payment> captor =
                ArgumentCaptor.forClass(Payment.class);

        verify(paymentRepository).save(captor.capture());

        Payment savedPayment = captor.getValue();

        assertSame(paymentResponse, result);
        assertEquals(42L, savedPayment.getOrderId());
        assertEquals(
                0,
                savedPayment.getMoney()
                        .getAmount()
                        .compareTo(new BigDecimal("150.00"))
        );
        assertEquals("USD", savedPayment.getMoney().getCurrency());
        assertEquals(
                "payment-key-123",
                savedPayment.getIdempotencyKey()
        );
        assertEquals(PaymentStatus.PENDING, savedPayment.getStatus());

        verify(paymentMapper).toPaymentResponse(savedPayment);
    }

    @Test
    void shouldReturnExistingPaymentForIdenticalIdempotentRequest() {
        Payment existingPayment = Payment.create(
                42L,
                new Money(new BigDecimal("150.00"), "USD"),
                "payment-key-123"
        );

        when(paymentRepository.findByIdempotencyKey(
                "payment-key-123"
        )).thenReturn(Optional.of(existingPayment));

        when(paymentMapper.toPaymentResponse(existingPayment))
                .thenReturn(paymentResponse);

        PaymentResponse result = paymentService.createPayment(request);

        assertSame(paymentResponse, result);

        verify(paymentRepository, never()).save(any(Payment.class));
        verify(paymentMapper).toPaymentResponse(existingPayment);
    }

    @Test
    void shouldRejectSameIdempotencyKeyWithDifferentOrder() {
        Payment existingPayment = Payment.create(
                42L,
                new Money(new BigDecimal("150.00"), "USD"),
                "payment-key-123"
        );

        request.setOrderId(99L);

        when(paymentRepository.findByIdempotencyKey(
                "payment-key-123"
        )).thenReturn(Optional.of(existingPayment));

        assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.createPayment(request)
        );

        verify(paymentRepository, never()).save(any(Payment.class));
        verifyNoInteractions(paymentMapper);
    }

    @Test
    void shouldRejectSameIdempotencyKeyWithDifferentAmount() {
        Payment existingPayment = Payment.create(
                42L,
                new Money(new BigDecimal("150.00"), "USD"),
                "payment-key-123"
        );

        request.setAmount(new BigDecimal("200.00"));

        when(paymentRepository.findByIdempotencyKey(
                "payment-key-123"
        )).thenReturn(Optional.of(existingPayment));

        assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.createPayment(request)
        );

        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void shouldRejectSameIdempotencyKeyWithDifferentCurrency() {
        Payment existingPayment = Payment.create(
                42L,
                new Money(new BigDecimal("150.00"), "USD"),
                "payment-key-123"
        );

        request.setCurrency("EUR");

        when(paymentRepository.findByIdempotencyKey(
                "payment-key-123"
        )).thenReturn(Optional.of(existingPayment));

        assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.createPayment(request)
        );

        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void shouldReturnPaymentWhenIdExists() {
        Payment payment = Payment.create(
                42L,
                new Money(new BigDecimal("150.00"), "USD"),
                "payment-key-123"
        );

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));

        when(paymentMapper.toPaymentResponse(payment))
                .thenReturn(paymentResponse);

        PaymentResponse result = paymentService.getPaymentById(1L);

        assertSame(paymentResponse, result);

        verify(paymentRepository).findById(1L);
        verify(paymentMapper).toPaymentResponse(payment);
    }

    @Test
    void shouldThrowWhenPaymentDoesNotExist() {
        when(paymentRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                java.util.NoSuchElementException.class,
                () -> paymentService.getPaymentById(999L)
        );

        verifyNoInteractions(paymentMapper);
    }

    @Test
    void shouldCancelPendingPayment() {
        Payment payment = Payment.create(
                42L,
                new Money(new BigDecimal("150.00"), "USD"),
                "payment-key-123"
        );

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));

        when(paymentRepository.save(payment)).thenReturn(payment);

        when(paymentMapper.toPaymentResponse(payment))
                .thenReturn(paymentResponse);

        PaymentResponse result = paymentService.cancelPayment(1L);

        assertSame(paymentResponse, result);
        assertEquals(PaymentStatus.CANCELLED, payment.getStatus());

        verify(paymentRepository).save(payment);
        verify(paymentMapper).toPaymentResponse(payment);
    }

    @Test
    void shouldRejectCancellationWhenPaymentIsProcessing() {
        Payment payment = Payment.create(
                42L,
                new Money(new BigDecimal("150.00"), "USD"),
                "payment-key-123"
        );

        payment.startProcessing();

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));

        assertThrows(
                IllegalStateException.class,
                () -> paymentService.cancelPayment(1L)
        );

        verify(paymentRepository, never()).save(any(Payment.class));
        verifyNoInteractions(paymentMapper);
    }
}
