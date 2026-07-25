package com.example.ecommerce_backend.modules.notification.controller;

import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.modules.notification.dto.NotificationResponse;
import com.example.ecommerce_backend.modules.notification.service.NotificationService;
import com.example.ecommerce_backend.modules.user.entity.User;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/notifications")
@Profile("!test")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getNotifications(
            @AuthenticationPrincipal User user, Pageable pageable) {
        Page<NotificationResponse> notifications = notificationService.getUserNotifications(user.getId(), pageable);
        return ApiResponse.success(notifications, "Notifications retrieved successfully");
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(@AuthenticationPrincipal User user) {
        long count = notificationService.getUnreadCount(user.getId());
        return ApiResponse.success(Map.of("count", count), "Unread count retrieved");
    }

    @PatchMapping("/{uuid}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable String uuid, @AuthenticationPrincipal User user) {
        notificationService.markAsRead(uuid, user.getId());
        return ApiResponse.success(null, "Notification marked as read");
    }

    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(@AuthenticationPrincipal User user) {
        notificationService.markAllAsRead(user.getId());
        return ApiResponse.success(null, "All notifications marked as read");
    }
}
