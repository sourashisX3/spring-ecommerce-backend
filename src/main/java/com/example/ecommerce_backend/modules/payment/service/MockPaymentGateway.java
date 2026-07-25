package com.example.ecommerce_backend.modules.payment.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Component
public class MockPaymentGateway implements PaymentGatewayProvider {

    @Override
    public PaymentResult processPayment(Long orderId, BigDecimal amount, String currency, Map<String, String> metadata) {
        String transactionId = "MOCK-TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new PaymentResult(true, transactionId, "Payment processed successfully via mock gateway");
    }

    @Override
    public PaymentResult processRefund(String gatewayTransactionId, BigDecimal amount, String reason) {
        String refundId = "MOCK-RFD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new PaymentResult(true, refundId, "Refund processed successfully via mock gateway");
    }
}
