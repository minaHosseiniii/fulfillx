package com.fulfillx.notification_service.service;

import com.fulfillx.notification_service.domain.model.Notification;

import java.util.Optional;
import java.util.UUID;

public interface NotificationClaimService {
    Optional<Notification> claimNotification(UUID eventId);
}
