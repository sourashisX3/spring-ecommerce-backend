package com.example.ecommerce_backend.modules.payment.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Payment response")
public class PaymentResponse {
    @Schema(description = "Payment ID", example = "1")
    private Long id;
    @Schema(description = "Payment UUID", example = "payment-uuid-123")
    private String uuid;
    @Schema(description = "Order ID", example = "1")
    private Long orderId;
    @Schema(description = "User ID", example = "1")
    private Long userId;
    @Schema(description = "Payment gateway details")
    private PaymentGatewayResponse gateway;
    @Schema(description = "Payment amount", example = "99.99")
    private BigDecimal amount;
    @Schema(description = "Currency code", example = "USD")
    private String currency;
    @Schema(description = "Payment status", example = "COMPLETED")
    private String status;
    @Schema(description = "Payment method", example = "CREDIT_CARD")
    private String method;
    @Schema(description = "Gateway transaction ID", example = "txn_123456")
    private String gatewayTransactionId;
    @Schema(description = "Payment timestamp")
    private Instant paidAt;
    @Schema(description = "Creation timestamp")
    private Instant createdAt;
    @Schema(description = "Update timestamp")
    private Instant updatedAt;

    @JsonProperty("isActive")
    public boolean isActive() { return "COMPLETED".equals(status); }
}
