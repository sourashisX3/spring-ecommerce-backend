package com.example.ecommerce_backend.modules.coupon.dto.response;

import com.example.ecommerce_backend.modules.discount.dto.response.DiscountTypeResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response containing coupon details")
public class CouponResponse {

    @Schema(description = "Unique identifier of the coupon", example = "coupon-uuid-123")
    private String uuid;
    @Schema(description = "Unique coupon code", example = "SUMMER20")
    private String code;
    @Schema(description = "Description of the coupon", example = "20% off on summer collection")
    private String description;
    @Schema(description = "Discount type associated with the coupon")
    private DiscountTypeResponse discountType;
    @Schema(description = "Value of the discount", example = "20.00")
    private BigDecimal discountValue;
    @Schema(description = "Minimum order amount required", example = "50.00")
    private BigDecimal minOrderAmount;
    @Schema(description = "Maximum discount amount", example = "100.00")
    private BigDecimal maxDiscount;
    @Schema(description = "Maximum number of times the coupon can be used", example = "100")
    private Integer usageLimit;
    @Schema(description = "Maximum number of times per user", example = "1")
    private Integer usageLimitPerUser;
    @Schema(description = "Total number of times the coupon has been used", example = "10")
    private Integer totalUsed;
    @Schema(description = "Whether the coupon is active", example = "true")
    private boolean isActive;
    @Schema(description = "Whether the coupon is available globally", example = "false")
    private boolean isGlobal;
    @Schema(description = "Start date of coupon validity", example = "2024-01-01T00:00:00Z")
    private Instant validFrom;
    @Schema(description = "End date of coupon validity", example = "2024-12-31T23:59:59Z")
    private Instant validUntil;
    @Schema(description = "UUIDs of users the coupon is assigned to (empty for global coupons)", example = "[\"user-uuid-1\"]")
    private List<String> assignedUserUuids;
    @Schema(description = "Timestamp when the coupon was created", example = "2024-01-01T00:00:00Z")
    private Instant createdAt;
    @Schema(description = "Timestamp when the coupon was last updated", example = "2024-01-01T00:00:00Z")
    private Instant updatedAt;

    @JsonProperty("isActive")
    public boolean isActive() {
        return isActive;
    }

    @JsonProperty("isActive")
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    @JsonProperty("isGlobal")
    public boolean isGlobal() {
        return isGlobal;
    }

    @JsonProperty("isGlobal")
    public void setGlobal(boolean isGlobal) {
        this.isGlobal = isGlobal;
    }
}
