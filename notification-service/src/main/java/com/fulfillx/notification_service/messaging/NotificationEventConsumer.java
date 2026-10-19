package com.fulfillx.notification_service.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventConsumer {

    @KafkaListener(
            topics = "notification-events",
            groupId = "notification-service"
    )
    public void consume(String message) {
        System.out.println("received notification event: " + message);
    }
}
