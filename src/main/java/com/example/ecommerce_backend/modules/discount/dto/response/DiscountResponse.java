package com.example.ecommerce_backend.modules.discount.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class DiscountResponse {
    private Long id;
    private String uuid;
    private DiscountTypeResponse discountType;
    private BigDecimal discountValue;
    private BigDecimal minOrderAmount;
    private BigDecimal maxDiscount;
    private boolean isActive;
    private boolean isGlobal;
    private Instant validFrom;
    private Instant validUntil;
    private String description;
    private Instant createdAt;
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
