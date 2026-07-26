package com.example.ecommerce_backend.modules.returns.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request object for creating or updating a return type")
public class ReturnTypeRequest {
    @Schema(description = "Unique code for the return type", example = "REFUND", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String code;

    @Schema(description = "Name of the return type", example = "Refund", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String name;

    @Schema(description = "Description of the return type", example = "Full refund to original payment method")
    private String description;
}
