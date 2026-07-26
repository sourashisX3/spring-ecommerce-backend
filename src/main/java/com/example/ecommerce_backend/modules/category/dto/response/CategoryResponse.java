package com.example.ecommerce_backend.modules.category.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Category response")
public class CategoryResponse {

    @Schema(description = "Category ID", example = "1")
    private Long id;

    @Schema(description = "Category UUID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String uuid;

    @Schema(description = "Category name", example = "Electronics")
    private String name;

    @Schema(description = "Category slug", example = "electronics")
    private String slug;

    @Schema(description = "Category description", example = "Electronic devices and accessories")
    private String description;

    @Schema(description = "Category image URL", example = "https://example.com/category.png")
    private String imageUrl;

    @Schema(description = "Parent category slug", example = "root-category")
    private String parentSlug;

    @Schema(description = "Sort order", example = "1")
    private int sortOrder;

    private boolean isActive;

    @Schema(description = "Number of products", example = "100")
    private long productCount;

    @Schema(description = "Creation timestamp")
    private Instant createdAt;

    @Schema(description = "Last update timestamp")
    private Instant updatedAt;

    @Schema(description = "Child categories")
    private List<CategoryResponse> children;

    @JsonProperty("isActive")
    @Schema(description = "Whether the category is active", example = "true")
    public boolean isActive() {
        return isActive;
    }

    @JsonProperty("isActive")
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
}
