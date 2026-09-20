package com.fulfillx.order.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    private String productNameSnapshot;
    @Column(precision = 19, scale = 2)
    private BigDecimal unitPriceSnapshot;
    @Column(precision = 19, scale = 2)
    private BigDecimal subTotal;
    private Integer quantity;
    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;
}
