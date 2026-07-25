package com.example.ecommerce_backend.core.event;

public record PaymentFailedEvent(Object source, Long userId, String paymentUuid, String orderUuid) {
}
