package com.fulfillx.inventory_service.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "inventory_reservations",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_reservation_order_saga_product",
                        columnNames = {"order_id", "saga_id", "product_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InventoryReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "saga_id", nullable = false)
    private UUID sagaId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant updatedAt;

    public InventoryReservation(
            Long orderId,
            UUID sagaId,
            Long productId,
            Integer quantity
    ) {
        if (orderId == null || sagaId == null || productId == null) {
            throw new IllegalArgumentException("Reservation identifiers cannot be null");
        }

        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        this.orderId = orderId;
        this.sagaId = sagaId;
        this.productId = productId;
        this.quantity = quantity;
        this.status = ReservationStatus.RESERVED;
        this.createdAt = Instant.now();
    }

    public void release() {
        requireStatus(ReservationStatus.RESERVED);
        this.status = ReservationStatus.RELEASED;
        this.updatedAt = Instant.now();
    }

    public void deduct() {
        requireStatus(ReservationStatus.RESERVED);
        this.status = ReservationStatus.DEDUCTED;
        this.updatedAt = Instant.now();
    }

    private void requireStatus(ReservationStatus expected) {
        if (this.status != expected) {
            throw new IllegalStateException(
                    "Reservation must be in status " + expected
            );
        }
    }
}
