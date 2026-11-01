package com.fulfillx.inventory_service.repository;

import com.fulfillx.inventory_service.domain.model.OutboxEvent;
import com.fulfillx.inventory_service.domain.model.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
    List<OutboxEvent> findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus status);
}
