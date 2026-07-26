package com.example.ecommerce_backend.modules.notification.dto;

import com.example.ecommerce_backend.modules.notification.entity.Notification;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
@Schema(description = "Notification response")
public class NotificationResponse {
    @Schema(description = "Notification UUID", example = "notif-uuid-123")
    private String uuid;
    @Schema(description = "Notification type", example = "ORDER_CONFIRMATION")
    private String type;
    @Schema(description = "Notification title", example = "Order Confirmed")
    private String title;
    @Schema(description = "Notification body", example = "Your order has been confirmed")
    private String body;
    @Schema(description = "Deep link URL", example = "/orders/ORD-001")
    private String deepLink;
    @Schema(description = "Whether notification has been read", example = "false")
    private boolean isRead;
    @Schema(description = "Creation timestamp")
    private Instant createdAt;
    @Schema(description = "Read timestamp")
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
