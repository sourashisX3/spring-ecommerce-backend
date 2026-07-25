package com.example.ecommerce_backend.modules.brand.mapper;

import com.example.ecommerce_backend.modules.brand.dto.response.BrandResponse;
import com.example.ecommerce_backend.modules.brand.entity.Brand;

public class BrandMapper {

    private BrandMapper() {
    }

    public static BrandResponse toResponse(Brand brand) {
        return BrandResponse.builder()
                .id(brand.getId())
                .uuid(brand.getUuid())
                .name(brand.getName())
                .slug(brand.getSlug())
                .description(brand.getDescription())
                .logoUrl(brand.getLogoUrl())
                .website(brand.getWebsite())
                .isActive(brand.isActive())
                .createdAt(brand.getCreatedAt())
                .updatedAt(brand.getUpdatedAt())
                .build();
    }
}
