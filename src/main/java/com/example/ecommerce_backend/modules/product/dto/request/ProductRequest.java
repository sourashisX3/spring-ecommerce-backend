package com.example.ecommerce_backend.modules.product.dto.request;

import com.example.ecommerce_backend.modules.image.dto.request.ImageRequest;
import com.example.ecommerce_backend.modules.variant.dto.request.VariantRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Schema(description = "Request object for creating or updating a product")
public class ProductRequest {

    @Schema(description = "Stock Keeping Unit", example = "WH-1000XM5", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "SKU is required")
    private String sku;

    @Schema(description = "Product name", example = "Wireless Headphones", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Product name is required")
    private String name;

    @Schema(description = "Product detailed description", example = "High-quality wireless headphones with noise cancellation")
    private String description;
    @Schema(description = "Short product description", example = "Premium wireless headphones")
    private String shortDescription;

    @Schema(description = "Base price of the product", example = "99.99", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.00", message = "Base price must be >= 0")
    private BigDecimal basePrice;

    @Schema(description = "Product attribute key-value pairs")
    private Map<String, String> attributes;

    @Schema(description = "Category slug", example = "electronics", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Category slug is required")
    private String categorySlug;

    @Schema(description = "Brand slug", example = "sony", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Brand slug is required")
    private String brandSlug;

    @Schema(description = "List of tag slugs", example = "[\"new-arrival\", \"best-seller\"]")
    private List<String> tagSlugs;

    private boolean isFeatured;

    @Schema(description = "Product variants")
    private List<VariantRequest> variants;
    @Schema(description = "Product images")
    private List<ImageRequest> images;

    @JsonProperty("isFeatured")
    @Schema(description = "Whether the product is featured", example = "true")
    public boolean isFeatured() {
        return isFeatured;
    }

    @JsonProperty("isFeatured")
    public void setFeatured(boolean isFeatured) {
        this.isFeatured = isFeatured;
    }
}
