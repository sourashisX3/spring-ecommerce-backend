package com.example.ecommerce_backend.modules.payment.dto.response;

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
@Schema(description = "Refund response")
public class RefundResponse {
    @Schema(description = "Refund ID", example = "1")
    private Long id;
    @Schema(description = "Refund UUID", example = "refund-uuid-123")
    private String uuid;
    @Schema(description = "Payment ID", example = "1")
    private Long paymentId;
    @Schema(description = "Return request ID", example = "1")
    private Long returnRequestId;
    @Schema(description = "Refund amount", example = "99.99")
    private BigDecimal amount;
    @Schema(description = "Refund reason", example = "Customer requested refund")
    private String reason;
    @Schema(description = "Refund status", example = "COMPLETED")
    private String status;
    @Schema(description = "Gateway refund ID", example = "rfnd_123456")
    private String gatewayRefundId;
    @Schema(description = "Refund timestamp")
    private Instant refundedAt;
    @Schema(description = "Creation timestamp")
    private Instant createdAt;
    @Schema(description = "Update timestamp")
    private Instant updatedAt;
}
