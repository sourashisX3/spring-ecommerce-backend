package com.example.ecommerce_backend.modules.payment.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long id;
    private String uuid;
    private Long orderId;
    private Long userId;
    private PaymentGatewayResponse gateway;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String method;
    private String gatewayTransactionId;
    private Instant paidAt;
    private Instant createdAt;
    private Instant updatedAt;

    @JsonProperty("isActive")
    public boolean isActive() { return "COMPLETED".equals(status); }
}
