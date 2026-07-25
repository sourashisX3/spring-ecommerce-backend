package com.example.ecommerce_backend.modules.tag.mapper;

import com.example.ecommerce_backend.modules.tag.dto.response.TagResponse;
import com.example.ecommerce_backend.modules.tag.entity.Tag;

public class TagMapper {

    private TagMapper() {
    }

    public static TagResponse toResponse(Tag tag) {
        return TagResponse.builder()
                .id(tag.getId())
                .uuid(tag.getUuid())
                .name(tag.getName())
                .slug(tag.getSlug())
                .isActive(tag.isActive())
                .createdAt(tag.getCreatedAt())
                .updatedAt(tag.getUpdatedAt())
                .build();
    }
}
