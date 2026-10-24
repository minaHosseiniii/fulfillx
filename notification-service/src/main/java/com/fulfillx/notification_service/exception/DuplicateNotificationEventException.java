
package com.fulfillx.notification_service.exception;

public class DuplicateNotificationEventException
        extends RuntimeException {

    public DuplicateNotificationEventException(String message) {
        super(message);
    }
}
