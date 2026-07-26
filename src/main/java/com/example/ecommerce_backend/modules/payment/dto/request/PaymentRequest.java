package com.example.ecommerce_backend.modules.payment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Payment request")
public class PaymentRequest {
    @NotNull(message = "Order ID is required")
    @Schema(description = "Order ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long orderId;

    @NotBlank(message = "Gateway code is required")
    @Schema(description = "Gateway code", example = "STRIPE", requiredMode = Schema.RequiredMode.REQUIRED)
    private String gatewayCode;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    @Schema(description = "Payment amount", example = "99.99", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    @Schema(description = "Currency code", example = "USD")
    private String currency;
    @Schema(description = "Payment method", example = "CREDIT_CARD")
    private String method;
}
