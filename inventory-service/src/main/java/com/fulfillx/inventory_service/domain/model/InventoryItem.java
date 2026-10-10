package com.fulfillx.inventory_service.domain.model;

import com.fulfillx.inventory_service.exception.InsufficientStockException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "inventory_items",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_inventory_product_id",
                        columnNames = "product_id"
                ),
                @UniqueConstraint(
                        name = "uk_inventory_sku",
                        columnNames = "sku"
                )
        }
)
@Getter
@NoArgsConstructor
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false, length = 100)
    private String sku;

    @Column(name = "available_quantity", nullable = false)
    private int availableQuantity;

    @Column(name = "reserved_quantity", nullable = false)
    private int reservedQuantity;

    public InventoryItem(Long productId, String sku, int availableQuantity) {
        this.productId = productId;
        this.sku = sku;
        this.availableQuantity = availableQuantity;
    }

    public void reserve(int quantity) {
        validateQuantity(quantity);

        if (availableQuantity < quantity) {
            throw new InsufficientStockException(
                    "Insufficient available stock"
            );
        }

        availableQuantity -= quantity;
        reservedQuantity += quantity;
    }

    public void release(int quantity) {
        validateQuantity(quantity);

        if (reservedQuantity < quantity) {
            throw new InsufficientStockException(
                    "Insufficient reserved stock to release"
            );
        }

        reservedQuantity -= quantity;
        availableQuantity += quantity;
    }

    public void deduct(int quantity) {
        validateQuantity(quantity);

        if (reservedQuantity < quantity) {
            throw new InsufficientStockException(
                    "Insufficient reserved stock to deduct"
            );
        }

        reservedQuantity -= quantity;
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be positive"
            );
        }
    }
}
