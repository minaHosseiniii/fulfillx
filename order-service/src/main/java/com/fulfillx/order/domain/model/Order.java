package com.fulfillx.order.domain.model;

import com.fulfillx.order.domain.model.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long customerId;

    @Column(name = "saga_id", unique = true)
    private UUID sagaId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    @Embedded
    private ShippingAddress shippingAddress;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(
                    name = "amount",
                    column = @Column(
                            name = "total_amount",
                            precision = 19,
                            scale = 2,
                            nullable = false
                    )
            ),
            @AttributeOverride(
                    name = "currency",
                    column = @Column(
                            name = "currency",
                            length = 3,
                            nullable = false
                    )
            )
    })
    private Money totalAmount;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<OrderItem> items = new ArrayList<>();

    public Order(
            Long customerId,
            ShippingAddress shippingAddress,
            String currency
    ) {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }

        if (shippingAddress == null) {
            throw new IllegalArgumentException(
                    "Shipping address cannot be null"
            );
        }

        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException(
                    "Currency cannot be null or empty"
            );
        }

        this.customerId = customerId;
        this.shippingAddress = shippingAddress;
        this.orderStatus = OrderStatus.PENDING;
        this.totalAmount = new Money(
                BigDecimal.ZERO,
                currency
        );
    }

    public void addItem(OrderItem item) {
        if (orderStatus != OrderStatus.PENDING) {
            throw new IllegalStateException(
                    "Cannot modify order after processing has started"
            );
        }

        if (item == null) {
            throw new IllegalArgumentException(
                    "Order item cannot be null"
            );
        }

        if (!item.getUnitPriceSnapshot()
                .getCurrency()
                .equals(totalAmount.getCurrency())) {

            throw new IllegalArgumentException(
                    "Order item currency must match order currency"
            );
        }

        item.attachTo(this);
        items.add(item);

        recalculateTotal();
    }

    public void removeItem(OrderItem item) {
        if (orderStatus != OrderStatus.PENDING) {
            throw new IllegalStateException(
                    "Cannot modify order after processing has started"
            );
        }

        if (item == null) {
            return;
        }

        if (items.remove(item)) {
            recalculateTotal();
        }
    }

    public void startInventoryReservation(UUID sagaId) {
        requireStatus(OrderStatus.PENDING);
        if (sagaId == null) {
            throw new IllegalArgumentException("Saga ID cannot be null");
        }
        this.sagaId = sagaId;
        this.orderStatus = OrderStatus.INVENTORY_RESERVATION_PENDING;
    }

    public void markInventoryReserved() {
        requireStatus(OrderStatus.INVENTORY_RESERVATION_PENDING);
        this.orderStatus = OrderStatus.INVENTORY_RESERVED;
    }

    public void startPayment() {
        requireStatus(OrderStatus.INVENTORY_RESERVED);
        this.orderStatus = OrderStatus.PAYMENT_PENDING;
    }

    public void markPaymentFailed() {
        requireStatus(OrderStatus.PAYMENT_PENDING);
        this.orderStatus = OrderStatus.PAYMENT_FAILED;
    }

    public void confirm() {
        requireStatus(OrderStatus.PAYMENT_PENDING);
        this.orderStatus = OrderStatus.CONFIRMED;
    }

    public void ship() {
        requireStatus(OrderStatus.CONFIRMED);
        this.orderStatus = OrderStatus.SHIPPED;
    }

    public void deliver() {
        requireStatus(OrderStatus.SHIPPED);
        this.orderStatus = OrderStatus.DELIVERED;
    }

    public void markInventoryReservationFailed() {
        requireStatus(OrderStatus.INVENTORY_RESERVATION_PENDING);
        this.orderStatus = OrderStatus.INVENTORY_RESERVATION_FAILED;
    }

    public void startCancellation() {
        if (orderStatus == OrderStatus.DELIVERED ||
        orderStatus == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Delivered or cancelled order cannot enter cancellation");
        }
        this.orderStatus = OrderStatus.CANCELLATION_PENDING;
    }

    public void completeCancellation() {
        requireStatus(OrderStatus.CANCELLATION_PENDING);
        this.orderStatus = OrderStatus.CANCELLED;
    }

    public void cancel() {
        if (orderStatus == OrderStatus.DELIVERED) {
            throw new IllegalStateException(
                    "Delivered order cannot be cancelled"
            );
        }

        if (orderStatus == OrderStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Order is already cancelled"
            );
        }

        this.orderStatus = OrderStatus.CANCELLED;
    }

    public Money calculateTotalAmount() {
        if (items == null || items.isEmpty()) {
            throw new IllegalStateException("Cannot calculate total amount for an empty order");
        }

        String currency = items.get(0)
                .getUnitPriceSnapshot()
                .getCurrency();
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItem item : items) {
            Money unitPrice = item.getUnitPriceSnapshot();
            if (!currency.equals(unitPrice.getCurrency())) {
                throw new IllegalStateException("All order items must have the same currency");
            }
            totalAmount = totalAmount.add(item.getSubTotal());
        }
        return new Money(totalAmount, currency);
    }

    private void requireStatus(OrderStatus expectedStatus) {
        if (orderStatus != expectedStatus) {
            throw new IllegalStateException(
                    "Invalid order status transition from "
                            + orderStatus
                            + " to required status "
                            + expectedStatus
            );
        }
    }


    private void recalculateTotal() {
        BigDecimal total = items.stream()
                .map(OrderItem::getSubTotal)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );

        this.totalAmount = new Money(
                total,
                totalAmount.getCurrency()
        );
    }
}