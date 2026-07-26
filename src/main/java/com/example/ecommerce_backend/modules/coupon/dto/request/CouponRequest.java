package com.example.ecommerce_backend.modules.coupon.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Schema(description = "Request to create or update a coupon")
public class CouponRequest {

    @NotBlank(message = "Code is required")
    @Schema(description = "Unique coupon code", example = "SUMMER20", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @Schema(description = "Description of the coupon", example = "20% off on summer collection")
    private String description;

    @NotBlank(message = "Discount type code is required")
    @Schema(description = "Code of the discount type", example = "PERCENTAGE", requiredMode = Schema.RequiredMode.REQUIRED)
    private String discountTypeCode;

    @NotNull(message = "Discount value is required")
    @Positive(message = "Discount value must be positive")
    @Schema(description = "Value of the discount", example = "20.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal discountValue;

    @Schema(description = "Minimum order amount required", example = "50.00")
    private BigDecimal minOrderAmount;

    @Schema(description = "Maximum discount amount", example = "100.00")
    private BigDecimal maxDiscount;

    @Schema(description = "Maximum number of times the coupon can be used", example = "100")
    private Integer usageLimit;

    @Schema(description = "Maximum number of times per user", example = "1")
    private Integer usageLimitPerUser;

    @NotNull(message = "Valid from is required")
    @Schema(description = "Start date of coupon validity", example = "2024-01-01T00:00:00Z", requiredMode = Schema.RequiredMode.REQUIRED)
    private Instant validFrom;

    @NotNull(message = "Valid until is required")
    @Schema(description = "End date of coupon validity", example = "2024-12-31T23:59:59Z", requiredMode = Schema.RequiredMode.REQUIRED)
    private Instant validUntil;

    @Schema(description = "Whether the coupon is available globally", example = "true")
    private Boolean isGlobal;
}
