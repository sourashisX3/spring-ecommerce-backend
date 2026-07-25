package com.example.ecommerce_backend.modules.coupon.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class CouponRequest {

    @NotBlank(message = "Code is required")
    private String code;

    private String description;

    @NotBlank(message = "Discount type code is required")
    private String discountTypeCode;

    @NotNull(message = "Discount value is required")
    @Positive(message = "Discount value must be positive")
    private BigDecimal discountValue;

    private BigDecimal minOrderAmount;

    private BigDecimal maxDiscount;

    private Integer usageLimit;

    private Integer usageLimitPerUser;

    @NotNull(message = "Valid from is required")
    private Instant validFrom;

    @NotNull(message = "Valid until is required")
    private Instant validUntil;

    private Boolean isGlobal;
}
