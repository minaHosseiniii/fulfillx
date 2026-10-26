package com.fulfillx.notification_service.service.impl;

import com.fulfillx.notification_service.domain.model.Notification;
import com.fulfillx.notification_service.repository.NotificationRepository;
import com.fulfillx.notification_service.service.NotificationClaimService;
import com.fulfillx.notification_service.service.NotificationDeliveryService;
import com.fulfillx.notification_service.service.NotificationResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRetryScheduler {

    private static final int MAX_ATTEMPTS = 3;

    private final NotificationRepository notificationRepository;
    private final NotificationClaimService notificationClaimService;
    private final NotificationDeliveryService notificationDeliveryService;
    private final NotificationResultService notificationResultService;

    @Scheduled(fixedDelayString = "${notification.retry.fixed-delay-ms:30000}")
    public void retryNotifications() {
        notificationRepository.failExpiredProcessingAfterMaxAttempts(
                Instant.now(),
                MAX_ATTEMPTS
        );

        List<UUID> eventIds =
                notificationRepository.findRetryableEventIds(
                        Instant.now(),
                        MAX_ATTEMPTS
                );

        for (UUID eventId : eventIds) {
            try {
                retryNotification(eventId);
            } catch (RuntimeException e) {
                log.error(
                        "Unexpected error while retrying notification {}",
                        eventId,
                        e
                );
            }
        }
    }

    private void retryNotification(UUID eventId) {
        Optional<Notification> claimedNotification =
                notificationClaimService.claimNotification(eventId);

        if (claimedNotification.isEmpty()) {
            return;
        }

        Notification notification = claimedNotification.get();
        UUID processingToken = notification.getProcessingToken();

        try {
            notificationDeliveryService.deliverNotification(notification);
        } catch (RuntimeException e) {
            String error = e.getMessage();

            if (error == null || error.isBlank()) {
                error = e.getClass().getSimpleName();
            }

            try {
                boolean recorded = notificationResultService.markFailed(
                        eventId,
                        processingToken,
                        error
                );

                if (!recorded) {
                    log.warn(
                            "Could not record failure because the processing "
                                    + "token is no longer valid: {}",
                            eventId
                    );
                }
            } catch (RuntimeException persistenceError) {
                log.error(
                        "Could not persist failure for notification {}",
                        eventId,
                        persistenceError
                );
            }

            return;
        }

        try {
            boolean recorded = notificationResultService.markSent(
                    eventId,
                    processingToken
            );

            if (!recorded) {
                log.warn(
                        "Email delivery completed, but SENT status was not "
                                + "recorded because the processing token "
                                + "is no longer valid: {}",
                        eventId
                );
            }
        } catch (RuntimeException e) {
            log.error(
                    "Email delivery completed, but SENT status could not "
                            + "be persisted for notification {}",
                    eventId,
                    e
            );
        }
    }
}