
package com.fulfillx.notification_service.service.impl;

import com.fulfillx.notification_service.domain.model.Notification;
import com.fulfillx.notification_service.repository.NotificationRepository;
import com.fulfillx.notification_service.service.NotificationDeliveryService;
import com.fulfillx.notification_service.service.NotificationSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class NotificationDeliveryServiceImpl
        implements NotificationDeliveryService {

    private final Map<String, NotificationSender> sendersByChannel;
    private final NotificationRepository notificationRepository;

    public NotificationDeliveryServiceImpl(
            List<NotificationSender> notificationSenders,
            NotificationRepository notificationRepository
    ) {
        this.sendersByChannel = notificationSenders.stream()
                .collect(Collectors.toMap(
                        NotificationSender::channel,
                        Function.identity()
                ));

        this.notificationRepository = notificationRepository;
    }

    @Override
    public void deliverNotification(Notification notification) {
        NotificationSender sender =
                sendersByChannel.get(notification.getChannel());

        if (sender == null) {
            notification.markAsFailed();
            notificationRepository.save(notification);

            throw new IllegalStateException(
                    "No sender configured for channel: "
                            + notification.getChannel()
            );
        }

        try {
            sender.sendNotification(notification);
        } catch (RuntimeException exception) {
            notification.markAsFailed();
            notificationRepository.save(notification);
            throw exception;
        }

        notification.markAsSent();
        notificationRepository.save(notification);
    }
}
