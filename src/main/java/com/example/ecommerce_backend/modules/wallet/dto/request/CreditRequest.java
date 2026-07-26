package com.example.ecommerce_backend.modules.wallet.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Credit request")
public class CreditRequest {

    @NotNull
    @Positive
    @Schema(description = "Credit amount", example = "500.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    @Schema(description = "Transaction description", example = "Wallet top-up")
    private String description;
}
