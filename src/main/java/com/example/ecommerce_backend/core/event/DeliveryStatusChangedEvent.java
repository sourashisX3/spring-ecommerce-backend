package com.example.ecommerce_backend.core.event;

public record DeliveryStatusChangedEvent(Object source, Long userId, String deliveryUuid, String newStatus) {
}
