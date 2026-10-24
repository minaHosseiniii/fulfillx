package com.fulfillx.notification_service.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notifications",
indexes = {
        @Index(name = "idx_notifications_created_at", columnList = "created_at")
})
@Getter
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "event_id", nullable = false, unique = true)
    private UUID eventId;
    @Column(nullable = false)
    private String recipient;
    @Column(nullable = false)
    private String channel;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;
    private String subject;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status = NotificationStatus.PENDING;
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Instant createdAt;
    @Column(name = "sent_at")
    private Instant sentAt;

    public Notification(
            UUID eventId,
            String recipient,
            String channel,
            String subject,
            String message
    ) {
        this.eventId = eventId;
        this.recipient = recipient;
        this.channel = channel;
        this.subject = subject;
        this.message = message;
        this.status = NotificationStatus.PENDING;
    }

    public void markAsSent() {
        this.status = NotificationStatus.SENT;
        this.sentAt = Instant.now();
    }

    public void markAsFailed() {
        this.status = NotificationStatus.FAILED;
        this.sentAt = null;
    }
}
