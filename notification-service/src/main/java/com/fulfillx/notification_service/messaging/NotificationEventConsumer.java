package com.fulfillx.notification_service.messaging;

import com.fulfillx.notification_service.events.NotificationEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventConsumer {

    @KafkaListener(
            topics = "notification-events",
            groupId = "notification-service"
    )
    public void consume(NotificationEvent notificationEvent) {
        System.out.println("Received notification event:");
        System.out.println("Event: " + notificationEvent.event());
        System.out.println("Message: " + notificationEvent.message());
    }
}
