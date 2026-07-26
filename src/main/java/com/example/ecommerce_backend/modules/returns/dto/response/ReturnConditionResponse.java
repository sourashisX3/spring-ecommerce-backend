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
@Schema(description = "Response object for return condition data")
public class ReturnConditionResponse {
    @Schema(description = "Return condition UUID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String uuid;
    @Schema(description = "Return condition code", example = "DAMAGED")
    private String code;
    @Schema(description = "Return condition name", example = "Damaged Item")
    private String name;
    @Schema(description = "Return condition description", example = "Item arrived damaged or defective")
    private String description;
    @Schema(description = "Whether the condition is active", example = "true")
    private boolean isActive;
    @Schema(description = "Creation timestamp")
    private Instant createdAt;
    @Schema(description = "Last update timestamp")
    private Instant updatedAt;
}
