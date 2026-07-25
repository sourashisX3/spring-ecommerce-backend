package com.example.ecommerce_backend.modules.discount.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
public class DiscountRequest {
    @NotBlank(message = "Discount type code is required")
    private String discountTypeCode;

    @NotNull(message = "Discount value is required")
    @Positive(message = "Discount value must be positive")
    private BigDecimal discountValue;

    private BigDecimal minOrderAmount;
    private BigDecimal maxDiscount;
    private Boolean isGlobal;
    private Instant validFrom;
    private Instant validUntil;
    private String description;

    private List<String> userUuids;
}
