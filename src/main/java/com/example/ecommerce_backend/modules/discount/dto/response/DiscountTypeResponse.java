package com.example.ecommerce_backend.modules.discount.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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
@Schema(description = "Response containing discount type details")
public class DiscountTypeResponse {
    @Schema(description = "Internal ID of the discount type", example = "1")
    private Long id;
    @Schema(description = "Unique identifier of the discount type", example = "dtype-uuid-123")
    private String uuid;
    @Schema(description = "Unique code of the discount type", example = "PERCENTAGE")
    private String code;
    @Schema(description = "Name of the discount type", example = "Percentage Discount")
    private String name;
    @Schema(description = "Description of the discount type", example = "A percentage-based discount")
    private String description;
    @Schema(description = "Computation type", example = "PERCENTAGE")
    private String computation;
    @Schema(description = "JSON schema for additional configuration")
    private String configSchema;
    @Schema(description = "Whether the discount type is active", example = "true")
    private boolean isActive;
    @Schema(description = "Timestamp when the discount type was created", example = "2024-01-01T00:00:00Z")
    private Instant createdAt;
    @Schema(description = "Timestamp when the discount type was last updated", example = "2024-01-01T00:00:00Z")
    private Instant updatedAt;

    @JsonProperty("isActive")
    public boolean isActive() { return isActive; }

    @JsonProperty("isActive")
    public void setActive(boolean isActive) { this.isActive = isActive; }
}
