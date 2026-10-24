package com.fulfillx.notification_service.service.impl;

import com.fulfillx.notification_service.domain.model.Notification;
import com.fulfillx.notification_service.service.NotificationSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailSender implements NotificationSender {
    private final JavaMailSender mailSender;

    public EmailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public String channel() {
        return "EMAIL";
    }

    @Override
    public void sendNotification(Notification notification) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(notification.getRecipient());
        message.setSubject(notification.getSubject());
        message.setText(notification.getMessage());

        mailSender.send(message);
    }
}
