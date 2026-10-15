package com.fulfillx.payment_service.controller;

import com.fulfillx.payment_service.dto.CreatePaymentRequest;
import com.fulfillx.payment_service.dto.PaymentResponse;
import com.fulfillx.payment_service.service.PaymentService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Validated
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @Valid @RequestBody CreatePaymentRequest request
    ) {
        PaymentResponse response = paymentService.createPayment(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPaymentById(
            @PathVariable @Positive Long paymentId
    ) {
        PaymentResponse response =
                paymentService.getPaymentById(paymentId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-idempotency-key")
    public ResponseEntity<PaymentResponse> getPaymentByIdempotencyKey(
            @RequestParam
            @NotBlank
            @Size(max = 100)
            String key
    ) {
        PaymentResponse response =
                paymentService.getPaymentByIdempotencyKey(key);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{paymentId}/cancel")
    public ResponseEntity<PaymentResponse> cancelPayment(
            @PathVariable @Positive Long paymentId
    ) {
        PaymentResponse response =
                paymentService.cancelPayment(paymentId);

        return ResponseEntity.ok(response);
    }
}
