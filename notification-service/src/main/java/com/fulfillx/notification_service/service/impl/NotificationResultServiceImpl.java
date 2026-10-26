package com.fulfillx.notification_service.service.impl;

import com.fulfillx.notification_service.repository.NotificationRepository;
import com.fulfillx.notification_service.service.NotificationResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationResultServiceImpl implements NotificationResultService {
    private final NotificationRepository notificationRepository;

    @Override
    public boolean markSent(UUID eventId, UUID processingToken) {
        return (notificationRepository.markSentIfTokenMatches(eventId, processingToken)) == 1;
    }

    @Override
    public boolean markFailed(UUID eventId, UUID processingToken, String errorMessage) {
        return (notificationRepository.markFailedIfTokenMatches(eventId, processingToken, errorMessage)) == 1;
    }
}
