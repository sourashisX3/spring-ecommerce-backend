package com.example.ecommerce_backend.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatusRequest {

    @NotNull(message = "isActive is required")
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
