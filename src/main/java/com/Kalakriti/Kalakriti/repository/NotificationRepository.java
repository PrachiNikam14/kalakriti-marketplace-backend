package com.Kalakriti.Kalakriti.repository;

import com.Kalakriti.Kalakriti.entity.Notification;
import com.Kalakriti.Kalakriti.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserOrderByCreatedAtDesc(User user);
}