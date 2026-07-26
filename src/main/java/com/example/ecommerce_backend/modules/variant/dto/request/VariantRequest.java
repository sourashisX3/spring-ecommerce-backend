package com.example.ecommerce_backend.modules.variant.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Schema(description = "Variant request")
public class VariantRequest {

    @NotBlank(message = "Variant SKU is required")
    @Schema(description = "Variant SKU", example = "SKU-12345", requiredMode = Schema.RequiredMode.REQUIRED)
    private String sku;

    @NotBlank(message = "Variant name is required")
    @Schema(description = "Variant name", example = "Size Large", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @DecimalMin(value = "0.00", message = "Price must be >= 0")
    @Schema(description = "Variant price", example = "29.99")
    private BigDecimal price;

    @Min(value = 0, message = "Stock must be >= 0")
    @Schema(description = "Stock quantity", example = "100")
    private int stock;

    @Schema(description = "Variant attributes")
    private Map<String, String> attributes;

    @Schema(description = "Sort order", example = "0")
    private int sortOrder;

    @Schema(description = "Whether this is the default variant", example = "false")
    private boolean isDefault;

    @JsonProperty("isDefault")
    public boolean isDefault() {
        return isDefault;
    }

    @JsonProperty("isDefault")
    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
}
