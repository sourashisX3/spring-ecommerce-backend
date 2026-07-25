package com.example.ecommerce_backend.core.event;

public record PaymentProcessedEvent(Object source, Long userId, String paymentUuid, String orderUuid) {
}
