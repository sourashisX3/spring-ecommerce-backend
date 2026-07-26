package com.example.ecommerce_backend.modules.notification.controller;

import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.modules.notification.dto.NotificationResponse;
import com.example.ecommerce_backend.modules.notification.service.NotificationService;
import com.example.ecommerce_backend.modules.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
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
@Tag(name = "Notification", description = "Notification API")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    @Operation(summary = "Get notifications", description = "Retrieve paginated notifications for the authenticated user")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getNotifications(
            @AuthenticationPrincipal User user, @ParameterObject Pageable pageable) {
        Page<NotificationResponse> notifications = notificationService.getUserNotifications(user.getId(), pageable);
        return ApiResponse.success(notifications, "Notifications retrieved successfully");
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get unread count", description = "Get the count of unread notifications for the authenticated user")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(@AuthenticationPrincipal User user) {
        long count = notificationService.getUnreadCount(user.getId());
        return ApiResponse.success(Map.of("count", count), "Unread count retrieved");
    }

    @PatchMapping("/{uuid}/read")
    @Operation(summary = "Mark notification as read", description = "Mark a specific notification as read by UUID")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable String uuid, @AuthenticationPrincipal User user) {
        notificationService.markAsRead(uuid, user.getId());
        return ApiResponse.success(null, "Notification marked as read");
    }

    @PatchMapping("/read-all")
    @Operation(summary = "Mark all as read", description = "Mark all notifications as read for the authenticated user")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(@AuthenticationPrincipal User user) {
        notificationService.markAllAsRead(user.getId());
        return ApiResponse.success(null, "All notifications marked as read");
    }
}
