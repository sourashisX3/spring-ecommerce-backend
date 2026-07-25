package com.example.ecommerce_backend.modules.product.mapper;

import com.example.ecommerce_backend.modules.product.dto.response.TagResponse;
import com.example.ecommerce_backend.modules.product.entity.Tag;

public class TagMapper {

    private TagMapper() {
    }

    public static TagResponse toResponse(Tag tag) {
        return TagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .slug(tag.getSlug())
                .isActive(tag.isActive())
                .createdAt(tag.getCreatedAt())
                .updatedAt(tag.getUpdatedAt())
                .build();
    }
}
