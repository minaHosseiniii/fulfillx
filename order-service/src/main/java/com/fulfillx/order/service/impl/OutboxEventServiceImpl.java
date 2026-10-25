
package com.fulfillx.order.service.impl;

import com.fulfillx.order.domain.model.OutboxEvent;
import com.fulfillx.order.repository.OutboxEventRepository;
import com.fulfillx.order.service.OutboxEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboxEventServiceImpl implements OutboxEventService {

    private final OutboxEventRepository outboxEventRepository;

    @Transactional(readOnly = true)
    @Override
    public List<OutboxEvent> findPendingEvents() {
        return outboxEventRepository
                .findByPublishedAtIsNullOrderByCreatedAtAsc(
                        PageRequest.of(0, 50)
                );
    }

    @Transactional
    @Override
    public void markPublished(UUID eventId) {
        OutboxEvent event = outboxEventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalStateException(
                        "Outbox event not found: " + eventId
                ));

        event.markPublished();
    }


    @Transactional
    @Override
    public void recordFailure(UUID eventId, String error) {
        OutboxEvent event = outboxEventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalStateException(
                        "Outbox event not found: " + eventId
                ));

        event.recordFailure(error);
    }
}
