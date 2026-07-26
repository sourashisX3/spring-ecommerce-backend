package com.example.ecommerce_backend.modules.discount.dto.response;

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
@Schema(description = "Response containing discount details")
public class DiscountResponse {
    @Schema(description = "Internal ID of the discount", example = "1")
    private Long id;
    @Schema(description = "Unique identifier of the discount", example = "discount-uuid-123")
    private String uuid;
    @Schema(description = "Discount type details")
    private DiscountTypeResponse discountType;
    @Schema(description = "Value of the discount", example = "20.00")
    private BigDecimal discountValue;
    @Schema(description = "Minimum order amount required", example = "50.00")
    private BigDecimal minOrderAmount;
    @Schema(description = "Maximum discount amount", example = "100.00")
    private BigDecimal maxDiscount;
    @Schema(description = "Whether the discount is active", example = "true")
    private boolean isActive;
    @Schema(description = "Whether the discount is available globally", example = "false")
    private boolean isGlobal;
    @Schema(description = "Start date of discount validity", example = "2024-01-01T00:00:00Z")
    private Instant validFrom;
    @Schema(description = "End date of discount validity", example = "2024-12-31T23:59:59Z")
    private Instant validUntil;
    @Schema(description = "Description of the discount", example = "20% off on selected items")
    private String description;
    @Schema(description = "Timestamp when the discount was created", example = "2024-01-01T00:00:00Z")
    private Instant createdAt;
    @Schema(description = "Timestamp when the discount was last updated", example = "2024-01-01T00:00:00Z")
    private Instant updatedAt;

    @JsonProperty("isActive")
    public boolean isActive() { return isActive; }

    @JsonProperty("isActive")
    public void setActive(boolean isActive) { this.isActive = isActive; }

    @JsonProperty("isGlobal")
    public boolean isGlobal() { return isGlobal; }

    @JsonProperty("isGlobal")
    public void setGlobal(boolean isGlobal) { this.isGlobal = isGlobal; }
}
