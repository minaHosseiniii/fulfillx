package com.fulfillx.inventory_service.repository;

import com.fulfillx.inventory_service.domain.model.InventoryItem;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {
    Optional<InventoryItem> findByProductId(Long productId);
    Optional<InventoryItem> findBySku(String sku);
    boolean existsBySku(String sku);
    boolean existsByProductId(Long productId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select i from InventoryItem i
        where i.productId = :productId
""")
    Optional<InventoryItem> findByProductIdForUpdate(@Param("productId") Long productId);
}
