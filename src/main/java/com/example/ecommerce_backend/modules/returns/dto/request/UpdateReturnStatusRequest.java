package com.example.ecommerce_backend.modules.returns.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request object for updating a return request status")
public class UpdateReturnStatusRequest {
    @Schema(description = "New status for the return request", example = "APPROVED", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String status;

    @Schema(description = "Notes about the resolution", example = "Refund has been processed")
    private String resolutionNotes;
}
