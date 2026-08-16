package com.example.ecommerce_backend.modules.banner.mapper;

import com.example.ecommerce_backend.modules.banner.dto.response.BannerResponse;
import com.example.ecommerce_backend.modules.banner.entity.Banner;

public class BannerMapper {

    private BannerMapper() {
    }

    public static BannerResponse toResponse(Banner banner) {
        return BannerResponse.builder()
                .uuid(banner.getUuid())
                .title(banner.getTitle())
                .subtitle(banner.getSubtitle())
                .imageUrl(banner.getImageUrl())
                .linkType(banner.getLinkType())
                .linkValue(banner.getLinkValue())
                .sortOrder(banner.getSortOrder())
                .isActive(banner.isActive())
                .validFrom(banner.getValidFrom())
                .validUntil(banner.getValidUntil())
                .createdAt(banner.getCreatedAt())
                .updatedAt(banner.getUpdatedAt())
                .build();
    }
}