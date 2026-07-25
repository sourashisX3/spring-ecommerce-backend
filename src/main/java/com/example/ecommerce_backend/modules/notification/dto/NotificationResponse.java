package com.example.ecommerce_backend.modules.notification.dto;

import com.example.ecommerce_backend.modules.notification.entity.Notification;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class NotificationResponse {
    private String uuid;
    private String type;
    private String title;
    private String body;
    private String deepLink;
    private boolean isRead;
    private Instant createdAt;
    private Instant readAt;

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .uuid(notification.getUuid())
                .type(notification.getType())
                .title(notification.getTitle())
                .body(notification.getBody())
                .deepLink(notification.getDeepLink())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .readAt(notification.getReadAt())
                .build();
    }
}
