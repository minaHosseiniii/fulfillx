package com.fulfillx.inventory_service.repository;

import com.fulfillx.inventory_service.domain.model.InventoryReservation;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
                select r 
                from InventoryReservation r
                where r.orderId =:orderId
                and r.sagaId =:sagaId
                order by r.productId
            """)
    List<InventoryReservation> findAllByOrderIdAndSagaIdForUpdate(
            @Param("orderId") Long orderId,
            @Param("sagaId") UUID sagaId);
}
