package com.example.ecommerce_backend.modules.discount.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountTypeResponse {
    private Long id;
    private String uuid;
    private String code;
    private String name;
    private String description;
    private String computation;
    private String configSchema;
    private boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;

    @JsonProperty("isActive")
    public boolean isActive() { return isActive; }

    @JsonProperty("isActive")
    public void setActive(boolean isActive) { this.isActive = isActive; }
}
