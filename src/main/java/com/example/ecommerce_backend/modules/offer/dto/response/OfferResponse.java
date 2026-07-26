package com.example.ecommerce_backend.modules.offer.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.example.ecommerce_backend.modules.discount.dto.response.DiscountTypeResponse;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Offer response")
public class OfferResponse {

    @Schema(description = "Offer UUID", example = "offer-uuid-123")
    private String uuid;
    @Schema(description = "Offer title", example = "Summer Sale")
    private String title;
    @Schema(description = "Offer description", example = "20% off on all items")
    private String description;
    @Schema(description = "Discount type details")
    private DiscountTypeResponse discountType;
    @Schema(description = "Discount value", example = "20.00")
    private BigDecimal discountValue;
    @Schema(description = "Minimum order amount", example = "100.00")
    private BigDecimal minOrderAmount;
    @Schema(description = "Maximum discount amount", example = "50.00")
    private BigDecimal maxDiscount;
    @Schema(description = "Usage limit", example = "100")
    private Integer usageLimit;
    @Schema(description = "Usage limit per user", example = "1")
    private Integer usageLimitPerUser;
    @Schema(description = "Total times used", example = "10")
    private Integer totalUsed;
    @Schema(description = "Whether offer is active", example = "true")
    private boolean isActive;
    @Schema(description = "Offer valid from timestamp")
    private Instant validFrom;
    @Schema(description = "Offer valid until timestamp")
    private Instant validUntil;
    @Schema(description = "Whether offer is global", example = "true")
    private boolean isGlobal;
    @Schema(description = "Applicable entity type", example = "PRODUCT")
    private String applicableTo;
    @Schema(description = "Applicable entity IDs", example = "uuid1,uuid2")
    private String applicableIds;
    @Schema(description = "Creation timestamp")
    private Instant createdAt;
    @Schema(description = "Update timestamp")
    private Instant updatedAt;
    @Schema(description = "Assigned user UUIDs")
    private List<String> assignedUserUuids;

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
