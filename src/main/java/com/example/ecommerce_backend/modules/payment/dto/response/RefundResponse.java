package com.example.ecommerce_backend.modules.payment.dto.response;

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
public class RefundResponse {
    private Long id;
    private String uuid;
    private Long paymentId;
    private Long returnRequestId;
    private BigDecimal amount;
    private String reason;
    private String status;
    private String gatewayRefundId;
    private Instant refundedAt;
    private Instant createdAt;
    private Instant updatedAt;
}
