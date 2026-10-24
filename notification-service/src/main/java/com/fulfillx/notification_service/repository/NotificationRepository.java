package com.fulfillx.notification_service.repository;

import com.fulfillx.notification_service.domain.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    boolean existsByEventId(UUID eventId);
}
