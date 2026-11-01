package com.fulfillx.inventory_service.repository;

import com.fulfillx.inventory_service.domain.model.InventoryReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryReservationRepository extends JpaRepository<InventoryReservation, Long> {
    List<InventoryReservation> findAllByOrderId(Long orderId);

    List<InventoryReservation> findAllBySagaId(UUID sagaId);

    Optional<InventoryReservation> findByOrderIdAndProductId(
            Long orderId,
            Long productId
    );

    boolean existsByOrderIdAndSagaId(Long orderId, UUID sagaId);

    List<InventoryReservation> findAllByOrderIdAndSagaId(
            Long orderId,
            UUID sagaId
    );
}
