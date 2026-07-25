package com.example.ecommerce_backend.modules.offer.mapper;

import com.example.ecommerce_backend.modules.discount.mapper.DiscountMapper;
import com.example.ecommerce_backend.modules.offer.dto.response.OfferResponse;
import com.example.ecommerce_backend.modules.offer.entity.Offer;
import com.example.ecommerce_backend.modules.user.entity.User;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class OfferMapper {

    private OfferMapper() {
    }

    public static OfferResponse toResponse(Offer offer) {
        return OfferResponse.builder()
                .uuid(offer.getUuid())
                .title(offer.getTitle())
                .description(offer.getDescription())
                .discountType(offer.getDiscountType() != null
                        ? DiscountMapper.toTypeResponse(offer.getDiscountType())
                        : null)
                .discountValue(offer.getDiscountValue())
                .minOrderAmount(offer.getMinOrderAmount())
                .maxDiscount(offer.getMaxDiscount())
                .usageLimit(offer.getUsageLimit())
                .usageLimitPerUser(offer.getUsageLimitPerUser())
                .totalUsed(offer.getTotalUsed())
                .isActive(offer.isActive())
                .validFrom(offer.getValidFrom())
                .validUntil(offer.getValidUntil())
                .isGlobal(offer.isGlobal())
                .applicableTo(offer.getApplicableTo())
                .applicableIds(offer.getApplicableIds())
                .createdAt(offer.getCreatedAt())
                .updatedAt(offer.getUpdatedAt())
                .build();
    }

    public static OfferResponse toResponse(Offer offer, List<User> assignedUsers) {
        List<String> userUuids = assignedUsers != null
                ? assignedUsers.stream().map(User::getUuid).collect(Collectors.toList())
                : Collections.emptyList();

        return OfferResponse.builder()
                .uuid(offer.getUuid())
                .title(offer.getTitle())
                .description(offer.getDescription())
                .discountType(offer.getDiscountType() != null
                        ? DiscountMapper.toTypeResponse(offer.getDiscountType())
                        : null)
                .discountValue(offer.getDiscountValue())
                .minOrderAmount(offer.getMinOrderAmount())
                .maxDiscount(offer.getMaxDiscount())
                .usageLimit(offer.getUsageLimit())
                .usageLimitPerUser(offer.getUsageLimitPerUser())
                .totalUsed(offer.getTotalUsed())
                .isActive(offer.isActive())
                .validFrom(offer.getValidFrom())
                .validUntil(offer.getValidUntil())
                .isGlobal(offer.isGlobal())
                .applicableTo(offer.getApplicableTo())
                .applicableIds(offer.getApplicableIds())
                .createdAt(offer.getCreatedAt())
                .updatedAt(offer.getUpdatedAt())
                .assignedUserUuids(userUuids)
                .build();
    }
}
