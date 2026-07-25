package com.example.ecommerce_backend.modules.product.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class ProductRequest {

    @NotBlank(message = "SKU is required")
    private String sku;

    @NotBlank(message = "Product name is required")
    private String name;

    private String description;
    private String shortDescription;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.00", message = "Base price must be >= 0")
    private BigDecimal basePrice;

    private Map<String, String> attributes;

    @NotBlank(message = "Category slug is required")
    private String categorySlug;

    @NotBlank(message = "Brand slug is required")
    private String brandSlug;

    private List<String> tagSlugs;

    private boolean isFeatured;

    private List<VariantRequest> variants;
    private List<ImageRequest> images;

    @JsonProperty("isFeatured")
    public boolean isFeatured() {
        return isFeatured;
    }

    @JsonProperty("isFeatured")
    public void setFeatured(boolean isFeatured) {
        this.isFeatured = isFeatured;
    }
}
