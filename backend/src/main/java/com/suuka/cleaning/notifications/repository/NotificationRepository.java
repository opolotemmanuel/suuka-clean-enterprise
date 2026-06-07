package com.suuka.cleaning.notifications.repository;

import com.suuka.cleaning.notifications.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId);

    List<Notification> findByUserIdAndReadFalse(UUID userId);

    long countByUserIdAndReadFalse(UUID userId);
}
