package com.example.ecommerce_backend.modules.discount.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Schema(description = "Request to create or update a discount")
public class DiscountRequest {
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
    @Schema(description = "Whether the discount is available globally", example = "true")
    private Boolean isGlobal;
    @Schema(description = "Start date of discount validity", example = "2024-01-01T00:00:00Z")
    private Instant validFrom;
    @Schema(description = "End date of discount validity", example = "2024-12-31T23:59:59Z")
    private Instant validUntil;
    @Schema(description = "Description of the discount", example = "20% off on selected items")
    private String description;

    @Schema(description = "List of user UUIDs to assign the discount to")
    private List<String> userUuids;
}
