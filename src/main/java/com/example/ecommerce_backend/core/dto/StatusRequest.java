package com.example.ecommerce_backend.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request payload for toggling active status")
public class StatusRequest {

    @NotNull(message = "isActive is required")
    @Schema(description = "Whether the entity should be active", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean isActive;

    @JsonProperty("isActive")
    public Boolean isActive() {
        return isActive;
    }

    @JsonProperty("isActive")
    public void setActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
