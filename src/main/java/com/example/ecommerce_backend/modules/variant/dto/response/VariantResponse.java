package com.example.ecommerce_backend.modules.variant.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VariantResponse {

    private Long id;
    private String uuid;
    private String sku;
    private String name;
    private BigDecimal price;
    private int stock;
    private Map<String, String> attributes;
    private boolean isActive;
    private boolean isDefault;
    private boolean selected;
    private int sortOrder;
    private Instant createdAt;
    private Instant updatedAt;

    @JsonProperty("isActive")
    public boolean isActive() {
        return isActive;
    }

    @JsonProperty("isActive")
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    @JsonProperty("isDefault")
    public boolean isDefault() {
        return isDefault;
    }

    @JsonProperty("isDefault")
    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
}
