package com.fulfillx.order.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@NoArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private String productNameSnapshot;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(
                    name = "amount",
                    column = @Column(
                            name = "unit_price",
                            precision = 19,
                            scale = 2,
                            nullable = false
                    )
            ),
            @AttributeOverride(
                    name = "currency",
                    column = @Column(
                            name = "unit_price_currency",
                            length = 3,
                            nullable = false
                    )
            )
    })
    private Money unitPriceSnapshot;

    @Column(
            precision = 19,
            scale = 2,
            nullable = false
    )
    private BigDecimal subTotal;

    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    public OrderItem(
            Long productId,
            String productNameSnapshot,
            Money unitPriceSnapshot,
            Integer quantity
    ) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }

        if (productNameSnapshot == null || productNameSnapshot.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }

        if (unitPriceSnapshot == null) {
            throw new IllegalArgumentException("Unit price cannot be null");
        }

        validateQuantity(quantity);

        this.productId = productId;
        this.productNameSnapshot = productNameSnapshot;
        this.unitPriceSnapshot = unitPriceSnapshot;
        this.quantity = quantity;
        this.subTotal = calculateSubTotal();
    }

    public void changeQuantity(Integer newQuantity) {
        validateQuantity(newQuantity);

        this.quantity = newQuantity;
        this.subTotal = calculateSubTotal();
    }

    private BigDecimal calculateSubTotal() {
        return unitPriceSnapshot.getAmount()
                .multiply(BigDecimal.valueOf(quantity));
    }

    void attachTo(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }

        this.order = order;
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than 0"
            );
        }
    }
}