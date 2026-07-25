package com.example.ecommerce_backend.modules.variant.mapper;

import com.example.ecommerce_backend.modules.variant.dto.request.VariantRequest;
import com.example.ecommerce_backend.modules.variant.dto.response.VariantResponse;
import com.example.ecommerce_backend.modules.variant.entity.ProductVariant;

import java.util.HashMap;

public class VariantMapper {

    private VariantMapper() {
    }

    public static VariantResponse toVariantResponse(ProductVariant variant) {
        return VariantResponse.builder()
                .id(variant.getId())
                .uuid(variant.getUuid())
                .sku(variant.getSku())
                .name(variant.getName())
                .price(variant.getPrice())
                .stock(variant.getStock())
                .attributes(variant.getAttributes())
                .isActive(variant.isActive())
                .isDefault(variant.isDefault())
                .sortOrder(variant.getSortOrder())
                .createdAt(variant.getCreatedAt())
                .updatedAt(variant.getUpdatedAt())
                .build();
    }

    public static ProductVariant toEntity(VariantRequest request) {
        return ProductVariant.builder()
                .sku(request.getSku())
                .name(request.getName())
                .price(request.getPrice())
                .stock(request.getStock())
                .attributes(request.getAttributes() != null ? request.getAttributes() : new HashMap<>())
                .isDefault(request.isDefault())
                .sortOrder(request.getSortOrder())
                .build();
    }
}
