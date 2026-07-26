package com.example.ecommerce_backend.modules.product.dto.response;

import com.example.ecommerce_backend.modules.image.dto.response.ImageResponse;
import com.example.ecommerce_backend.modules.variant.dto.response.VariantResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object for product data")
public class ProductResponse {

    @Schema(description = "Product UUID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String uuid;
    @Schema(description = "Stock Keeping Unit", example = "WH-1000XM5")
    private String sku;
    @Schema(description = "Product name", example = "Wireless Headphones")
    private String name;
    @Schema(description = "Product URL slug", example = "wireless-headphones")
    private String slug;
    @Schema(description = "Product detailed description", example = "High-quality wireless headphones with noise cancellation")
    private String description;
    @Schema(description = "Short product description", example = "Premium wireless headphones")
    private String shortDescription;
    @Schema(description = "Base price", example = "99.99")
    private BigDecimal basePrice;
    @Schema(description = "Minimum variant price", example = "79.99")
    private BigDecimal minVariantPrice;
    @Schema(description = "Maximum variant price", example = "149.99")
    private BigDecimal maxVariantPrice;
    @Schema(description = "Product attribute key-value pairs")
    private Map<String, String> attributes;
    private boolean isActive;
    private boolean isFeatured;
    @Schema(description = "URL of the primary product image", example = "https://example.com/image.jpg")
    private String primaryImage;
    @Schema(description = "Creation timestamp")
    private Instant createdAt;
    @Schema(description = "Last update timestamp")
    private Instant updatedAt;

    @Schema(description = "Product category summary")
    private CategorySummary category;
    @Schema(description = "Product brand summary")
    private BrandSummary brand;
    @Schema(description = "Product tags")
    private List<TagSummary> tags;
    @Schema(description = "Product variants")
    private List<VariantResponse> variants;
    @Schema(description = "Product images")
    private List<ImageResponse> images;
    @Schema(description = "Product review statistics")
    private ReviewStats reviewStats;
    @Schema(description = "Recent product reviews")
    private List<ReviewSummary> recentReviews;

    @JsonProperty("isActive")
    @Schema(description = "Whether the product is active", example = "true")
    public boolean isActive() {
        return isActive;
    }

    @JsonProperty("isActive")
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    @JsonProperty("isFeatured")
    @Schema(description = "Whether the product is featured", example = "false")
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
    @Schema(description = "Summary of a category")
    public static class CategorySummary {
        @Schema(description = "Category ID", example = "1")
        private Long id;
        @Schema(description = "Category name", example = "Electronics")
        private String name;
        @Schema(description = "Category slug", example = "electronics")
        private String slug;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Summary of a brand")
    public static class BrandSummary {
        @Schema(description = "Brand ID", example = "1")
        private Long id;
        @Schema(description = "Brand name", example = "Sony")
        private String name;
        @Schema(description = "Brand slug", example = "sony")
        private String slug;
        @Schema(description = "Brand logo URL", example = "https://example.com/logo.png")
        private String logoUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Summary of a tag")
    public static class TagSummary {
        @Schema(description = "Tag ID", example = "1")
        private Long id;
        @Schema(description = "Tag name", example = "New Arrival")
        private String name;
        @Schema(description = "Tag slug", example = "new-arrival")
        private String slug;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Review statistics for a product")
    public static class ReviewStats {
        @Schema(description = "Average rating", example = "4.5")
        private double averageRating;
        @Schema(description = "Total review count", example = "42")
        private int totalCount;
        @Schema(description = "Rating distribution map")
        private Map<Integer, Long> ratingDistribution;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Summary of a review")
    public static class ReviewSummary {
        @Schema(description = "Review UUID", example = "r1e2v3u4-e5f6-7890-abcd-ef1234567890")
        private String uuid;
        @Schema(description = "Review rating", example = "5")
        private int rating;
        @Schema(description = "Review title", example = "Great product!")
        private String title;
        @Schema(description = "Review comment", example = "I love this product")
        private String comment;
        @Schema(description = "Reviewer name", example = "John Doe")
        private String userName;
        @Schema(description = "Review creation timestamp")
        private Instant createdAt;
    }
}
