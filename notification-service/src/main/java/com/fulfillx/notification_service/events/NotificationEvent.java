package com.fulfillx.notification_service.events;

public record NotificationEvent(
        String event,
        String message
) {
}
