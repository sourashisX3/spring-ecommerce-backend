package com.example.ecommerce_backend.modules.notification.service;

import com.example.ecommerce_backend.core.event.*;
import com.example.ecommerce_backend.modules.notification.dto.NotificationResponse;
import com.example.ecommerce_backend.modules.notification.entity.Notification;
import com.example.ecommerce_backend.modules.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Profile("!test")
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(NotificationRepository notificationRepository,
                               SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponse> getUserNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(NotificationResponse::from);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Transactional
    public void markAsRead(String notificationUuid, Long userId) {
        notificationRepository.findByUuidAndUserId(notificationUuid, userId)
                .ifPresent(notification -> {
                    notification.setRead(true);
                    notification.setReadAt(java.time.Instant.now());
                    notificationRepository.save(notification);
                });
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsRead(userId);
    }

    @Async
    @EventListener
    @Transactional
    public void handleOrderCreated(OrderCreatedEvent event) {
        sendAndPush(event.userId(), "ORDER_CONFIRMED",
                "Order Confirmed", "Your order has been placed successfully",
                "ecommerce://orders/" + event.orderUuid());
    }

    @Async
    @EventListener
    @Transactional
    public void handleOrderStatusChanged(OrderStatusChangedEvent event) {
        sendAndPush(event.userId(), "ORDER_STATUS_CHANGED",
                "Order Status Updated", "Your order is now: " + event.newStatus(),
                "ecommerce://orders/" + event.orderUuid());
    }

    @Async
    @EventListener
    @Transactional
    public void handlePaymentProcessed(PaymentProcessedEvent event) {
        sendAndPush(event.userId(), "PAYMENT_RECEIVED",
                "Payment Received", "Your payment has been processed successfully",
                "ecommerce://orders/" + event.orderUuid());
    }

    @Async
    @EventListener
    @Transactional
    public void handlePaymentFailed(PaymentFailedEvent event) {
        sendAndPush(event.userId(), "PAYMENT_FAILED",
                "Payment Failed", "Your payment could not be processed. Please try again.",
                "ecommerce://orders/" + event.orderUuid());
    }

    @Async
    @EventListener
    @Transactional
    public void handleDeliveryStatusChanged(DeliveryStatusChangedEvent event) {
        sendAndPush(event.userId(), "DELIVERY_UPDATED",
                "Delivery Updated", "Your delivery is now: " + event.newStatus(),
                "ecommerce://orders/");
    }

    @Async
    @EventListener
    @Transactional
    public void handleUserRegistered(UserRegisteredEvent event) {
        log.info("User registered event received for userId: {}", event.userId());
    }

    private void sendAndPush(Long userId, String type, String title, String body, String deepLink) {
        try {
            Notification notification = Notification.builder()
                    .userId(userId)
                    .type(type)
                    .title(title)
                    .body(body)
                    .deepLink(deepLink)
                    .build();

            notification = notificationRepository.save(notification);

            NotificationResponse response = NotificationResponse.from(notification);

            messagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    "/queue/notifications",
                    response
            );
        } catch (Exception e) {
            log.error("Failed to send notification to user {}: {}", userId, e.getMessage());
        }
    }
}
