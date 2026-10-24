
package com.fulfillx.notification_service.service.impl;

import com.fulfillx.notification_service.domain.model.Notification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class EmailSenderIntegrationTest {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailSender emailSender;

    @Test
    void shouldSendEmailToMailtrapSandbox() {
        assertNotNull(mailSender);

        Notification notification = new Notification(
                UUID.randomUUID(),
                "test@example.com",
                "EMAIL",
                "FulfillX - Mailtrap Test",
                "This is a test email from FulfillX notification-service."
        );

        emailSender.sendNotification(notification);
    }
}
