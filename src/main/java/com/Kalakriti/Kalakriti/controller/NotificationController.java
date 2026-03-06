package com.Kalakriti.Kalakriti.controller;

import com.Kalakriti.Kalakriti.entity.Notification;
import com.Kalakriti.Kalakriti.entity.User;
import com.Kalakriti.Kalakriti.repository.NotificationRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;

    public NotificationController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @GetMapping
    public List<Notification> getUserNotifications(@AuthenticationPrincipal User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @PutMapping("/{id}/read")
    public String markAsRead(@PathVariable Long id) {

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setRead(true);
        notificationRepository.save(notification);

        return "Notification marked as read";
    }
}