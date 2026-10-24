package com.fulfillx.notification_service.service.impl;

import com.fulfillx.notification_service.domain.model.Notification;
import com.fulfillx.notification_service.events.NotificationEvent;
import com.fulfillx.notification_service.events.NotificationEventType;
import com.fulfillx.notification_service.exception.InvalidNotificationEventException;
import com.fulfillx.notification_service.repository.NotificationRepository;
import com.fulfillx.notification_service.service.NotificationProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationProcessingServiceImpl implements NotificationProcessingService {
    private final NotificationRepository notificationRepository;

    @Override
    public void process(NotificationEvent notificationEvent) {
        if (notificationEvent == null
        || notificationEvent.eventId() == null
        || notificationEvent.eventType() == null
        || notificationEvent.customerId() == null
        || notificationEvent.orderId() == null) {
            throw new IllegalArgumentException("Notification event is missing required fields");
        }

        if (notificationRepository.existsByEventId(notificationEvent.eventId())) {
            throw new InvalidNotificationEventException("Notification event already exists");
        }

        String subject = getSubject(notificationEvent.eventType());
        String message = String.format(
                "%s. order ID: %d%s",
                getMessage(notificationEvent.eventType()),
                notificationEvent.orderId(),
                notificationEvent.totalAmount() != null
                && notificationEvent.currency() != null
                ? String.format(
                        ". Total: %s %s",
                        notificationEvent.totalAmount(),
                        notificationEvent.currency()
                )
                        : ""
        );
        Notification notification = new Notification(
                notificationEvent.eventId(),
                "customerId: " + notificationEvent.customerId(),
                "IN_APP",
                subject,
                message
        );

        notificationRepository.save(notification);

    }

    private String getSubject(NotificationEventType eventType) {
        return switch (eventType) {
            case ORDER_CREATED -> "Order created";
            case ORDER_CONFIRMED -> "Order confirmed";
            case ORDER_PAYMENT_FAILED -> "Payment failed";
            case ORDER_SHIPPED -> "Order shipped";
            case ORDER_DELIVERED -> "Order delivered";
            case ORDER_CANCELLED -> "Order cancelled";
        };
    }

    private String getMessage(NotificationEventType eventType) {
        return switch (eventType) {
            case ORDER_CREATED ->
                    "Your order has been created";
            case ORDER_CONFIRMED ->
                    "Your order has been confirmed";
            case ORDER_PAYMENT_FAILED ->
                    "Payment for your order has failed";
            case ORDER_SHIPPED ->
                    "Your order has been shipped";
            case ORDER_DELIVERED ->
                    "Your order has been delivered";
            case ORDER_CANCELLED ->
                    "Your order has been cancelled";
        };
    }
}
