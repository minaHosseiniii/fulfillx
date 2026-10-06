package com.fulfillx.catalog_service.domain.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, unique = true, length = 100)
    private String sku;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductStatus status;

    public Product(
            String name,
            String description,
            String sku,
            BigDecimal price,
            String currency
    ) {
        this.name = name;
        this.description = description;
        this.sku = sku;
        this.price = price;
        this.currency = currency;
        this.status = ProductStatus.ACTIVE;
    }

    public void update(
            String name,
            String description,
            BigDecimal price,
            String currency
    ) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.currency = currency;
    }

    public void activate() {
        if (this.status == ProductStatus.ACTIVE) {
            return;
        }

        this.status = ProductStatus.ACTIVE;
    }

    public void deactivate() {
        if (this.status == ProductStatus.INACTIVE) {
            return;
        }

        this.status = ProductStatus.INACTIVE;
    }
}
