package com.example.ecommerce_backend.core.event;

public record OrderStatusChangedEvent(Object source, Long userId, String orderUuid, String newStatus) {
}
