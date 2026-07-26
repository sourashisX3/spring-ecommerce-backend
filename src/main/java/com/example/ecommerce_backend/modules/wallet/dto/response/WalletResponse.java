package com.example.ecommerce_backend.modules.wallet.dto.response;

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
@Schema(description = "Wallet response")
public class WalletResponse {

    @Schema(description = "Wallet ID", example = "1")
    private Long id;
    @Schema(description = "Wallet UUID", example = "550e8400-e29b-41d4-a716-446655440000")
    private String uuid;
    @Schema(description = "Wallet balance", example = "500.00")
    private BigDecimal balance;
    @Schema(description = "Currency", example = "USD")
    private String currency;

    @Schema(description = "Whether the wallet is active", example = "true")
    private boolean isActive;

    @JsonProperty("isActive")
    public boolean isActive() {
        return isActive;
    }

    @JsonProperty("isActive")
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    @Schema(description = "Creation timestamp")
    private Instant createdAt;
    @Schema(description = "Last update timestamp")
    private Instant updatedAt;
}
