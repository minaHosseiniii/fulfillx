package com.fulfillx.payment_service.repository;

import com.fulfillx.payment_service.domain.model.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
    List<OutboxEvent> findByPublishedFalseOrderByOccurredAtAsc();
    boolean existsByAggregateTypeAndAggregateIdAndEventType(
            String aggregateType,
            Long aggregateId,
            String eventType
    );
}
