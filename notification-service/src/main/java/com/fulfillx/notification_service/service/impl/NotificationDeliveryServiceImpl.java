package com.fulfillx.notification_service.service.impl;

import com.fulfillx.notification_service.domain.model.Notification;
import com.fulfillx.notification_service.service.NotificationDeliveryService;
import com.fulfillx.notification_service.service.NotificationSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationDeliveryServiceImpl implements NotificationDeliveryService {
    private final Map<String, NotificationSender> sendersByChannel;

    public NotificationDeliveryServiceImpl(List<NotificationSender> notificationSenders) {
        this.sendersByChannel = notificationSenders.stream().collect(Collectors.toMap(
                NotificationSender::channel,
                Function.identity()
        ));

    }

    @Override
    public void deliverNotification(Notification notification) {
        NotificationSender notificationSender = sendersByChannel.get(notification.getChannel());
        if (notificationSender == null) {
            throw new IllegalStateException("No sender configured for channel: " + notification.getChannel());
        }
        notificationSender.sendNotification(notification);
    }
}
