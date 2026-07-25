package com.example.ecommerce_backend.modules.notification.repository;

import com.example.ecommerce_backend.modules.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Optional<Notification> findByUuidAndUserId(String uuid, Long userId);

    long countByUserIdAndIsReadFalse(Long userId);

    @Modifying
    @Query(value = "UPDATE notifications SET is_read = true, read_at = NOW() WHERE user_id = :userId AND is_read = false", nativeQuery = true)
    void markAllAsRead(@Param("userId") Long userId);
}
