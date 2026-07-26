package com.example.ecommerce_backend.modules.variant.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Variant response")
public class VariantResponse {

    @Schema(description = "Variant ID", example = "1")
    private Long id;
    @Schema(description = "Variant UUID", example = "550e8400-e29b-41d4-a716-446655440000")
    private String uuid;
    @Schema(description = "Variant SKU", example = "SKU-12345")
    private String sku;
    @Schema(description = "Variant name", example = "Size Large")
    private String name;
    @Schema(description = "Variant price", example = "29.99")
    private BigDecimal price;
    @Schema(description = "Stock quantity", example = "100")
    private int stock;
    @Schema(description = "Variant attributes")
    private Map<String, String> attributes;
    @Schema(description = "Whether the variant is active", example = "true")
    private boolean isActive;
    @Schema(description = "Whether this is the default variant", example = "false")
    private boolean isDefault;
    @Schema(description = "Whether the variant is selected", example = "false")
    private boolean selected;
    @Schema(description = "Sort order", example = "0")
    private int sortOrder;
    @Schema(description = "Creation timestamp")
    private Instant createdAt;
    @Schema(description = "Last update timestamp")
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
