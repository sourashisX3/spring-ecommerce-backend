package com.example.ecommerce_backend.modules.product.dto.request;

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
}
