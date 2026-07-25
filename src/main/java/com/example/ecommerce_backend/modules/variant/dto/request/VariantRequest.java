package com.example.ecommerce_backend.modules.variant.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class VariantRequest {

    @NotBlank(message = "Variant SKU is required")
    private String sku;

    @NotBlank(message = "Variant name is required")
    private String name;

    @DecimalMin(value = "0.00", message = "Price must be >= 0")
    private BigDecimal price;

    @Min(value = 0, message = "Stock must be >= 0")
    private int stock;

    private Map<String, String> attributes;

    private int sortOrder;

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
