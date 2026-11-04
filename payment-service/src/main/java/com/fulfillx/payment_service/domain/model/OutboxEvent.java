package com.fulfillx.payment_service.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OutboxEvent {
    @Id
    private UUID id;
    @Column(nullable = false, length = 100)
    private String aggregateType;
    @Column(nullable = false)
    private Long aggregateId;
    @Column(nullable = false, length = 100)
    private String eventType;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;
    @Column(nullable = false)
    private Instant occurredAt;
    @Column(nullable = false)
    private boolean published;
    private Instant publishedAt;

    public OutboxEvent(
            UUID id,
            String aggregateType,
            Long aggregateId,
            String eventType,
            String payload
    ) {
        this.id = id;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.occurredAt = Instant.now();
        this.published = false;
    }

    public void markAsPublished() {
        this.published = true;
        this.publishedAt = Instant.now();
    }
}
