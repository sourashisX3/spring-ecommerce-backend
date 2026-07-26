package com.example.ecommerce_backend.modules.payment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Refund request")
public class RefundRequest {
    @NotNull(message = "Payment ID is required")
    @Schema(description = "Payment ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long paymentId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    @Schema(description = "Refund amount", example = "99.99", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    @Schema(description = "Refund reason", example = "Customer requested refund")
    private String reason;
    @Schema(description = "Return request ID", example = "1")
    private Long returnRequestId;
}
