package com.example.ecommerce_backend.modules.product.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private String uuid;
    private String sku;
    private String name;
    private String slug;
    private String description;
    private String shortDescription;
    private BigDecimal basePrice;
    private BigDecimal minVariantPrice;
    private BigDecimal maxVariantPrice;
    private Map<String, String> attributes;
    private boolean isActive;
    private boolean isFeatured;
    private String primaryImage;
    private Instant createdAt;
    private Instant updatedAt;

    private CategorySummary category;
    private BrandSummary brand;
    private List<TagSummary> tags;
    private List<VariantResponse> variants;
    private List<ImageResponse> images;

    @JsonProperty("isActive")
    public boolean isActive() {
        return isActive;
    }

    @JsonProperty("isActive")
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    @JsonProperty("isFeatured")
    public boolean isFeatured() {
        return isFeatured;
    }

    @JsonProperty("isFeatured")
    public void setFeatured(boolean isFeatured) {
        this.isFeatured = isFeatured;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategorySummary {
        private Long id;
        private String name;
        private String slug;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BrandSummary {
        private Long id;
        private String name;
        private String slug;
        private String logoUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TagSummary {
        private Long id;
        private String name;
        private String slug;
    }
}
