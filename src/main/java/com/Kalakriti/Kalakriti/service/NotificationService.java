package com.Kalakriti.Kalakriti.service;

import com.Kalakriti.Kalakriti.entity.*;
import com.Kalakriti.Kalakriti.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final SmsService smsService;

    public NotificationService(NotificationRepository notificationRepository,
                               EmailService emailService, SmsService smsService) {
        this.notificationRepository = notificationRepository;
        this.emailService = emailService;
        this.smsService = smsService;
    }

    @Async
    public void sendNotification(User user,
                                 NotificationType type,
                                 String title,
                                 String message) {

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);

        notificationRepository.save(notification);

        // 🔔 EMAIL
        emailService.sendEmail(
                user.getEmail(),
                title,
                message
        );

        // 📱 SMS (only if phone number exists)
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().isBlank()) {
            smsService.sendSms(
                    user.getPhoneNumber(),
                    message
            );
        }
    }
}