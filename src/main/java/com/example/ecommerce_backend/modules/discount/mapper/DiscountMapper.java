package com.example.ecommerce_backend.modules.discount.mapper;

import com.example.ecommerce_backend.modules.discount.dto.response.DiscountResponse;
import com.example.ecommerce_backend.modules.discount.dto.response.DiscountTypeResponse;
import com.example.ecommerce_backend.modules.discount.entity.Discount;
import com.example.ecommerce_backend.modules.discount.entity.DiscountType;

public class DiscountMapper {
    private DiscountMapper() {}

    public static DiscountTypeResponse toTypeResponse(DiscountType type) {
        return DiscountTypeResponse.builder()
                .id(type.getId())
                .uuid(type.getUuid())
                .code(type.getCode())
                .name(type.getName())
                .description(type.getDescription())
                .computation(type.getComputation())
                .configSchema(type.getConfigSchema())
                .isActive(type.isActive())
                .createdAt(type.getCreatedAt())
                .updatedAt(type.getUpdatedAt())
                .build();
    }

    public static DiscountResponse toResponse(Discount discount) {
        return DiscountResponse.builder()
                .id(discount.getId())
                .uuid(discount.getUuid())
                .discountType(toTypeResponse(discount.getDiscountType()))
                .discountValue(discount.getDiscountValue())
                .minOrderAmount(discount.getMinOrderAmount())
                .maxDiscount(discount.getMaxDiscount())
                .isActive(discount.isActive())
                .isGlobal(discount.isGlobal())
                .validFrom(discount.getValidFrom())
                .validUntil(discount.getValidUntil())
                .description(discount.getDescription())
                .createdAt(discount.getCreatedAt())
                .updatedAt(discount.getUpdatedAt())
                .build();
    }
}
