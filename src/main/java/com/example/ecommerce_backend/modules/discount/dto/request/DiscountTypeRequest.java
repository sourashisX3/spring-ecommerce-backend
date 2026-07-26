package com.example.ecommerce_backend.modules.discount.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request to create or update a discount type")
public class DiscountTypeRequest {
    @NotBlank(message = "Code is required")
    @Schema(description = "Unique code of the discount type", example = "PERCENTAGE", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @NotBlank(message = "Name is required")
    @Schema(description = "Name of the discount type", example = "Percentage Discount", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Description of the discount type", example = "A percentage-based discount")
    private String description;

    @NotBlank(message = "Computation type is required")
    @Schema(description = "Computation type for the discount", example = "PERCENTAGE", requiredMode = Schema.RequiredMode.REQUIRED)
    private String computation;

    @Schema(description = "JSON schema for additional configuration")
    private String configSchema;
}
