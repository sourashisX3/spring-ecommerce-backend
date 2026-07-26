package com.example.ecommerce_backend.modules.returns.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object for return type data")
public class ReturnTypeResponse {
    @Schema(description = "Return type UUID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String uuid;
    @Schema(description = "Return type code", example = "REFUND")
    private String code;
    @Schema(description = "Return type name", example = "Refund")
    private String name;
    @Schema(description = "Return type description", example = "Full refund to original payment method")
    private String description;
    @Schema(description = "Whether the return type is active", example = "true")
    private boolean isActive;
    @Schema(description = "Creation timestamp")
    private Instant createdAt;
    @Schema(description = "Last update timestamp")
    private Instant updatedAt;
}
