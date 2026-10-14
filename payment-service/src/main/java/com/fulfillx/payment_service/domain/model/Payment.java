package com.fulfillx.payment_service.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(
        name = "payments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_payment_idempotency_key",
                        columnNames = "idempotency_key"
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(
                    name = "amount",
                    column = @Column(
                            name = "amount",
                            nullable = false,
                            precision = 19,
                            scale = 2
                    )
            ),
            @AttributeOverride(
                    name = "currency",
                    column = @Column(
                            name = "currency",
                            nullable = false,
                            length = 3
                    )
            )
    })
    private Money money;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(name = "provider", length = 50)
    private String provider;

    @Column(name = "provider_payment_id", length = 150)
    private String providerPaymentId;

    @Column(
            name = "idempotency_key",
            nullable = false,
            length = 100
    )
    private String idempotencyKey;

    private Payment(
            Long orderId,
            Money money,
            String idempotencyKey
    ) {
        if (orderId == null || orderId <= 0) {
            throw new IllegalArgumentException(
                    "Order ID must be positive"
            );
        }

        this.money = Objects.requireNonNull(
                money,
                "Money must not be null"
        );

        if (money.getAmount().signum() <= 0) {
            throw new IllegalArgumentException(
                    "Payment amount must be greater than zero"
            );
        }

        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException(
                    "Idempotency key must not be blank"
            );
        }

        if (idempotencyKey.length() > 100) {
            throw new IllegalArgumentException(
                    "Idempotency key must not exceed 100 characters"
            );
        }

        this.orderId = orderId;
        this.idempotencyKey = idempotencyKey;
        this.status = PaymentStatus.PENDING;
    }

    public static Payment create(
            Long orderId,
            Money money,
            String idempotencyKey
    ) {
        return new Payment(orderId, money, idempotencyKey);
    }

    public void startProcessing() {
        requireStatus(PaymentStatus.PENDING);
        this.status = PaymentStatus.PROCESSING;
    }

    public void markSucceeded(
            String provider,
            String providerPaymentId
    ) {
        requireStatus(PaymentStatus.PROCESSING);

        this.provider = requireText(
                provider,
                "Provider must not be blank"
        );

        this.providerPaymentId = requireText(
                providerPaymentId,
                "Provider payment ID must not be blank"
        );

        this.status = PaymentStatus.SUCCEEDED;
    }

    public void markFailed() {
        requireStatus(PaymentStatus.PROCESSING);
        this.status = PaymentStatus.FAILED;
    }

    public void cancel() {
        requireStatus(PaymentStatus.PENDING);
        this.status = PaymentStatus.CANCELLED;
    }

    private void requireStatus(PaymentStatus expected) {
        if (this.status != expected) {
            throw new IllegalStateException(
                    "Expected status " + expected
                            + " but current status is " + this.status
            );
        }
    }

    private String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }

        return value;
    }
}
