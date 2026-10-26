package com.fulfillx.notification_service.service.impl;

import com.fulfillx.notification_service.client.CustomerServiceClient;
import com.fulfillx.notification_service.client.dto.CustomerServiceResponse;
import com.fulfillx.notification_service.domain.model.Notification;
import com.fulfillx.notification_service.events.NotificationEvent;
import com.fulfillx.notification_service.events.NotificationEventType;
import com.fulfillx.notification_service.exception.InvalidNotificationEventException;
import com.fulfillx.notification_service.repository.NotificationRepository;
import com.fulfillx.notification_service.service.NotificationClaimService;
import com.fulfillx.notification_service.service.NotificationDeliveryService;
import com.fulfillx.notification_service.service.NotificationProcessingService;
import com.fulfillx.notification_service.service.NotificationResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationProcessingServiceImpl implements NotificationProcessingService {
    private final NotificationRepository notificationRepository;
    private final CustomerServiceClient customerServiceClient;
    private final NotificationDeliveryService notificationDeliveryService;
    private final NotificationResultService notificationResultService;
    private final NotificationClaimService notificationClaimService;


    @Override
    public void process(NotificationEvent event) {
        validateEvent(event);
        ensureNotificationExists(event);

        Optional<Notification> claimedNotification =
                notificationClaimService.claimNotification(event.eventId());

        if (claimedNotification.isEmpty()) {
            return;
        }

        Notification claimed = claimedNotification.get();
        UUID processingToken = claimed.getProcessingToken();

        try {
            notificationDeliveryService.deliverNotification(claimed);
        } catch (RuntimeException e) {
            String error = e.getMessage();

            if (error == null || error.isBlank()) {
                error = e.getClass().getSimpleName();
            }

            boolean recorded = notificationResultService.markFailed(
                    event.eventId(),
                    processingToken,
                    error
            );

            if (!recorded) {
                throw new IllegalStateException(
                        "Notification failed, but its failure could not "
                                + "be recorded because the processing token "
                                + "is no longer valid: " + event.eventId(), e
                );
            }
            return;
        }

        boolean recorded = notificationResultService.markSent(
                event.eventId(),
                processingToken
        );

        if (!recorded) {
            throw new IllegalStateException(
                    "Email delivery completed, but SENT status could not "
                            + "be recorded because the processing token "
                            + "is no longer valid: " + event.eventId()
            );
        }
    }


    private void validateEvent(NotificationEvent event) {
        if (event == null
                || event.eventId() == null
                || event.eventType() == null
                || event.customerId() == null
                || event.orderId() == null) {

            throw new InvalidNotificationEventException(
                    "Notification event is missing required fields"
            );
        }
    }

    private String buildMessage(NotificationEvent event) {
        String message = String.format(
                "%s. Order ID: %d",
                getMessage(event.eventType()),
                event.orderId()
        );

        if (event.totalAmount() != null && event.currency() != null) {
            message += String.format(
                    ". Total: %s %s",
                    event.totalAmount(),
                    event.currency()
            );
        }

        return message;
    }

    private String getSubject(NotificationEventType eventType) {
        return switch (eventType) {
            case ORDER_CREATED -> "FulfillX - Order Created";
            case ORDER_CONFIRMED -> "FulfillX - Order Confirmed";
            case ORDER_PAYMENT_FAILED -> "FulfillX - Payment Failed";
            case ORDER_SHIPPED -> "FulfillX - Order Shipped";
            case ORDER_DELIVERED -> "FulfillX - Order Delivered";
            case ORDER_CANCELLED -> "FulfillX - Order Cancelled";
        };
    }

    private String getMessage(NotificationEventType eventType) {
        return switch (eventType) {
            case ORDER_CREATED -> "Your order has been created";
            case ORDER_CONFIRMED -> "Your order has been confirmed";
            case ORDER_PAYMENT_FAILED -> "Payment for your order failed";
            case ORDER_SHIPPED -> "Your order has been shipped";
            case ORDER_DELIVERED -> "Your order has been delivered";
            case ORDER_CANCELLED -> "Your order has been cancelled";
        };
    }

    private void ensureNotificationExists(NotificationEvent event) {
        if (notificationRepository.existsByEventId(event.eventId())) {
            return;
        }

        CustomerServiceResponse customer = customerServiceClient.getCustomerById(event.customerId());
        if (customer == null || customer.email() == null || customer.email().isBlank()) {
            throw new IllegalStateException("Customer email is missing for customer ID: " + event.customerId());
        }

        notificationRepository.insertIfAbsent(
                event.eventId(),
                customer.email(),
                "EMAIL",
                getSubject(event.eventType()),
                buildMessage(event));
    }
}