package com.fulfillx.notification_service.messaging;

import com.fulfillx.notification_service.events.NotificationEvent;
import com.fulfillx.notification_service.service.NotificationProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {
    private final NotificationProcessingService processingService;

    @KafkaListener(
            topics = "notification-events",
            groupId = "notification-service"
    )
    public void consume(NotificationEvent notificationEvent) {
        processingService.process(notificationEvent);
    }
}
