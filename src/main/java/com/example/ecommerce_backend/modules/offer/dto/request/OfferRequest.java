package com.example.ecommerce_backend.modules.offer.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Schema(description = "Offer request")
public class OfferRequest {

    @NotBlank(message = "Title is required")
    @Schema(description = "Offer title", example = "Summer Sale", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "Offer description", example = "20% off on all items")
    private String description;

    @NotBlank(message = "Discount type code is required")
    @Schema(description = "Discount type code", example = "PERCENTAGE", requiredMode = Schema.RequiredMode.REQUIRED)
    private String discountTypeCode;

    @NotNull(message = "Discount value is required")
    @Positive(message = "Discount value must be positive")
    @Schema(description = "Discount value", example = "20.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal discountValue;

    @Schema(description = "Minimum order amount", example = "100.00")
    private BigDecimal minOrderAmount;

    @Schema(description = "Maximum discount amount", example = "50.00")
    private BigDecimal maxDiscount;

    @Schema(description = "Usage limit", example = "100")
    private Integer usageLimit;

    @Schema(description = "Usage limit per user", example = "1")
    private Integer usageLimitPerUser;

    @NotNull(message = "Valid from is required")
    @Schema(description = "Offer valid from timestamp", requiredMode = Schema.RequiredMode.REQUIRED)
    private Instant validFrom;

    @NotNull(message = "Valid until is required")
    @Schema(description = "Offer valid until timestamp", requiredMode = Schema.RequiredMode.REQUIRED)
    private Instant validUntil;

    @Schema(description = "Whether offer is global", example = "true")
    private Boolean isGlobal;

    @Schema(description = "Applicable entity type", example = "PRODUCT")
    private String applicableTo;

    @Schema(description = "Applicable entity IDs", example = "uuid1,uuid2")
    private String applicableIds;
}
