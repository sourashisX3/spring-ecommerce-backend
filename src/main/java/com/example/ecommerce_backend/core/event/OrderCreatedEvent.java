package com.example.ecommerce_backend.core.event;

public record OrderCreatedEvent(Object source, Long userId, String orderUuid) {
}
