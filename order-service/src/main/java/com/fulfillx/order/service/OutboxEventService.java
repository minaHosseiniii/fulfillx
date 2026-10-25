package com.fulfillx.order.service;

import com.fulfillx.order.domain.model.OutboxEvent;

import java.util.List;
import java.util.UUID;

public interface OutboxEventService {
    List<OutboxEvent> findPendingEvents();
    void markPublished(UUID eventId);
    void recordFailure(UUID eventId, String error);
}
