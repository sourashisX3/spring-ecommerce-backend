package com.example.ecommerce_backend.modules.category.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Category request")
public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    @Schema(description = "Category name", example = "Electronics", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Category description", example = "Electronic devices and accessories")
    private String description;

    @Schema(description = "Category image URL", example = "https://example.com/category.png")
    private String imageUrl;

    @Schema(description = "Parent category slug", example = "root-category")
    private String parentSlug;

    @Schema(description = "Sort order", example = "1")
    private int sortOrder;
}
