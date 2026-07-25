package com.example.ecommerce_backend.modules.payment.service;

import java.math.BigDecimal;
import java.util.Map;

public interface PaymentGatewayProvider {
    PaymentResult processPayment(Long orderId, BigDecimal amount, String currency, Map<String, String> metadata);
    PaymentResult processRefund(String gatewayTransactionId, BigDecimal amount, String reason);
}

class PaymentResult {
    private final boolean success;
    private final String gatewayTransactionId;
    private final String message;

    public PaymentResult(boolean success, String gatewayTransactionId, String message) {
        this.success = success;
        this.gatewayTransactionId = gatewayTransactionId;
        this.message = message;
    }

    public boolean isSuccess() { return success; }
    public String getGatewayTransactionId() { return gatewayTransactionId; }
    public String getMessage() { return message; }
}
