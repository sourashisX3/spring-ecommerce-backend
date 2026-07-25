package com.example.ecommerce_backend.modules.product.mapper;

import com.example.ecommerce_backend.modules.product.dto.response.BrandResponse;
import com.example.ecommerce_backend.modules.product.entity.Brand;

public class BrandMapper {

    private BrandMapper() {
    }

    public static BrandResponse toResponse(Brand brand) {
        return BrandResponse.builder()
                .id(brand.getId())
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
