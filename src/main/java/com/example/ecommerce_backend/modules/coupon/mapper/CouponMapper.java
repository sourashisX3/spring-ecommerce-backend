package com.example.ecommerce_backend.modules.coupon.mapper;

import com.example.ecommerce_backend.modules.coupon.dto.response.CouponResponse;
import com.example.ecommerce_backend.modules.coupon.entity.Coupon;
import com.example.ecommerce_backend.modules.discount.mapper.DiscountMapper;

public class CouponMapper {

    private CouponMapper() {
    }

    public static CouponResponse toResponse(Coupon coupon) {
        return CouponResponse.builder()
                .uuid(coupon.getUuid())
                .code(coupon.getCode())
                .description(coupon.getDescription())
                .discountType(coupon.getDiscountType() != null
                        ? DiscountMapper.toTypeResponse(coupon.getDiscountType())
                        : null)
                .discountValue(coupon.getDiscountValue())
                .minOrderAmount(coupon.getMinOrderAmount())
                .maxDiscount(coupon.getMaxDiscount())
                .usageLimit(coupon.getUsageLimit())
                .usageLimitPerUser(coupon.getUsageLimitPerUser())
                .totalUsed(coupon.getTotalUsed())
                .isActive(coupon.isActive())
                .isGlobal(coupon.isGlobal())
                .validFrom(coupon.getValidFrom())
                .validUntil(coupon.getValidUntil())
                .createdAt(coupon.getCreatedAt())
                .updatedAt(coupon.getUpdatedAt())
                .build();
    }
}
