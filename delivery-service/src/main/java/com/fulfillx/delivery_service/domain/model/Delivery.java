package com.fulfillx.delivery_service.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(
        name = "deliveries",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_delivery_order_id",
                        columnNames = "order_id"
                ),
                @UniqueConstraint(
                        name = "uk_delivery_tracking_number",
                        columnNames = "tracking_number"
                )
        },
        indexes = {
                @Index(
                        name = "idx_delivery_status",
                        columnList = "delivery_status"
                )
        }
)
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Delivery extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false, length = 30)
    private DeliveryStatus deliveryStatus;

    @Embedded
    private DeliveryAddress deliveryAddress;

    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;

    @Column(name = "shipped_at")
    private Instant shippedAt;

    @Column(name = "delivered_at")
    private Instant deliveredAt;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    private Delivery(Long orderId, DeliveryAddress deliveryAddress) {
        if (orderId == null || orderId <= 0) {
            throw new IllegalArgumentException(
                    "Order ID must be positive");
        }

        if (deliveryAddress == null) {
            throw new IllegalArgumentException(
                    "Delivery address must not be null");
        }

        this.orderId = orderId;
        this.deliveryAddress = deliveryAddress;
        this.deliveryStatus = DeliveryStatus.PENDING;
    }

    public static Delivery create(
            Long orderId,
            DeliveryAddress deliveryAddress) {

        return new Delivery(orderId, deliveryAddress);
    }

    public void startPreparing() {
        requireStatus(DeliveryStatus.PENDING);
        this.deliveryStatus = DeliveryStatus.PREPARING;
    }

    public void markReadyForPickup() {
        requireStatus(DeliveryStatus.PREPARING);
        this.deliveryStatus = DeliveryStatus.READY_FOR_PICKUP;
    }

    public void markInTransit(String trackingNumber) {
        requireStatus(DeliveryStatus.READY_FOR_PICKUP);

        if (trackingNumber == null || trackingNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "Tracking number must not be blank");
        }

        this.trackingNumber = trackingNumber.trim();
        this.shippedAt = Instant.now();
        this.deliveryStatus = DeliveryStatus.IN_TRANSIT;
    }

    public void markDelivered() {
        requireStatus(DeliveryStatus.IN_TRANSIT);

        this.deliveredAt = Instant.now();
        this.deliveryStatus = DeliveryStatus.DELIVERED;
        this.failureReason = null;
    }

    public void markFailed(String reason) {
        if (deliveryStatus != DeliveryStatus.PREPARING
                && deliveryStatus != DeliveryStatus.READY_FOR_PICKUP
                && deliveryStatus != DeliveryStatus.IN_TRANSIT) {
            throw new IllegalStateException(
                    "Delivery cannot fail from status: " + deliveryStatus);
        }

        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException(
                    "Failure reason must not be blank");
        }

        this.failureReason = reason.trim();
        this.deliveryStatus = DeliveryStatus.FAILED;
    }

    public void markReturned() {
        if (deliveryStatus != DeliveryStatus.IN_TRANSIT
                && deliveryStatus != DeliveryStatus.FAILED) {
            throw new IllegalStateException(
                    "Delivery cannot be returned from status: "
                            + deliveryStatus);
        }

        this.deliveryStatus = DeliveryStatus.RETURNED;
    }

    public void cancel() {
        if (deliveryStatus != DeliveryStatus.PENDING
                && deliveryStatus != DeliveryStatus.PREPARING) {
            throw new IllegalStateException(
                    "Delivery cannot be cancelled from status: "
                            + deliveryStatus);
        }

        this.deliveryStatus = DeliveryStatus.CANCELLED;
    }

    private void requireStatus(DeliveryStatus expectedStatus) {
        if (deliveryStatus != expectedStatus) {
            throw new IllegalStateException(
                    "Expected status " + expectedStatus
                            + " but was " + deliveryStatus);
        }
    }
}