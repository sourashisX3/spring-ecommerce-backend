package com.example.ecommerce_backend.modules.returns.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request object for creating or updating a return condition")
public class ReturnConditionRequest {
    @Schema(description = "Unique code for the return condition", example = "DAMAGED", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String code;

    @Schema(description = "Name of the return condition", example = "Damaged Item", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String name;

    @Schema(description = "Description of the return condition", example = "Item arrived damaged or defective")
    private String description;
}
