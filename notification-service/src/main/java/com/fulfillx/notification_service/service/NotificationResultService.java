package com.fulfillx.notification_service.service;

import javax.management.remote.NotificationResult;
import java.util.UUID;

public interface NotificationResultService {
    boolean markSent(UUID eventId, UUID processingToken);
    boolean markFailed(UUID eventId, UUID processingToken, String errorMessage);
}
