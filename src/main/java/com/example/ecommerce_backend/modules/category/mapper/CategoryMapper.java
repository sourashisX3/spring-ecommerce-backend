package com.example.ecommerce_backend.modules.category.mapper;

import com.example.ecommerce_backend.modules.category.dto.response.CategoryResponse;
import com.example.ecommerce_backend.modules.category.entity.Category;

public class CategoryMapper {

    private CategoryMapper() {
    }

    public static CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .uuid(category.getUuid())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .imageUrl(category.getImageUrl())
                .parentSlug(category.getParent() != null ? category.getParent().getSlug() : null)
                .sortOrder(category.getSortOrder())
                .isActive(category.isActive())
                .productCount(0)
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}
