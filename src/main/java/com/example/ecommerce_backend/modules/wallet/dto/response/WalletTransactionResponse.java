package com.example.ecommerce_backend.modules.wallet.dto.response;

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
@Schema(description = "Wallet transaction response")
public class WalletTransactionResponse {

    @Schema(description = "Transaction ID", example = "1")
    private Long id;
    @Schema(description = "Transaction UUID", example = "550e8400-e29b-41d4-a716-446655440000")
    private String uuid;
    @Schema(description = "Transaction type", example = "CREDIT")
    private String type;
    @Schema(description = "Transaction amount", example = "500.00")
    private BigDecimal amount;
    @Schema(description = "Balance before transaction", example = "0.00")
    private BigDecimal balanceBefore;
    @Schema(description = "Balance after transaction", example = "500.00")
    private BigDecimal balanceAfter;
    @Schema(description = "Reference type", example = "ORDER")
    private String referenceType;
    @Schema(description = "Reference ID", example = "1")
    private Long referenceId;
    @Schema(description = "Transaction description", example = "Order payment")
    private String description;
    @Schema(description = "Creation timestamp")
    private Instant createdAt;
}
