package com.fulfillx.notification_service.service;

import com.fulfillx.notification_service.domain.model.Notification;

public interface NotificationSender {
    String channel();
    void sendNotification(Notification notification);
}
