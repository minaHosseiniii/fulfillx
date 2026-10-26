package com.fulfillx.notification_service.service.impl;

import com.fulfillx.notification_service.domain.model.Notification;
import com.fulfillx.notification_service.repository.NotificationRepository;
import com.fulfillx.notification_service.service.NotificationClaimService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationClaimServiceImpl implements NotificationClaimService {
    private static final int MAX_ATTEMPTS = 3;
    private static final Duration CLAIM_DURATION = Duration.ofMinutes(5);
    private final NotificationRepository notificationRepository;


    @Override
    @Transactional
    public Optional<Notification> claimNotification(UUID eventId) {
        Instant now = Instant.now();
        UUID processingToken = UUID.randomUUID();
        Instant leaseUntil = now.plus(CLAIM_DURATION);

        int updatedRows = notificationRepository.claimForProcessing(
                eventId,
                processingToken,
                leaseUntil,
                now,
                MAX_ATTEMPTS
        );
        if (updatedRows == 0) {
            return Optional.empty();
        }
        return notificationRepository.findByEventId(eventId);
    }
}
