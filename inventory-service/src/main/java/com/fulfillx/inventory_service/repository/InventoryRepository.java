package com.fulfillx.inventory_service.repository;

import com.fulfillx.inventory_service.domain.model.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {
    Optional<InventoryItem> findByProductId(Long productId);
    Optional<InventoryItem> findBySku(String sku);
    boolean existsBySku(String sku);
    boolean existsByProductId(Long productId);
}
