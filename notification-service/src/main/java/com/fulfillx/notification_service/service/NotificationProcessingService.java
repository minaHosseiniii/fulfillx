package com.fulfillx.notification_service.service;

import com.fulfillx.notification_service.events.NotificationEvent;

public interface NotificationProcessingService {
    void process(NotificationEvent notificationEvent);
}
