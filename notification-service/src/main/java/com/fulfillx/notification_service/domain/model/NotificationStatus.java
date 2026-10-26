package com.fulfillx.notification_service.domain.model;

import lombok.Getter;

@Getter
public enum NotificationStatus {
    PENDING,
    PROCESSING,
    SENT,
    FAILED
}
