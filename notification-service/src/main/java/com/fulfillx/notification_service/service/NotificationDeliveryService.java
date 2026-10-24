package com.fulfillx.notification_service.service;

import com.fulfillx.notification_service.domain.model.Notification;

public interface NotificationDeliveryService {
    void deliverNotification(Notification notification);
}
