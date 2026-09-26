package com.fulfillx.order.domain.model;

import com.fulfillx.order.domain.model.enums.OrderStatus;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

    public void startInventoryReservation() {
        requireStatus(OrderStatus.PENDING);
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