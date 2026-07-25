package com.example.ecommerce_backend.modules.product.mapper;

import com.example.ecommerce_backend.modules.product.dto.response.WishlistItemResponse;
import com.example.ecommerce_backend.modules.product.entity.ProductImage;
import com.example.ecommerce_backend.modules.product.entity.WishlistItem;

public class WishlistMapper {

    private WishlistMapper() {
    }

    public static WishlistItemResponse toResponse(WishlistItem item) {
        String primaryImage = item.getProduct().getImages() != null
                ? item.getProduct().getImages().stream()
                        .filter(ProductImage::isPrimary)
                        .findFirst()
                        .map(ProductImage::getImageUrl)
                        .orElse(null)
                : null;

        return WishlistItemResponse.builder()
                .uuid(item.getUuid())
                .productUuid(item.getProduct().getUuid())
                .productName(item.getProduct().getName())
                .productSlug(item.getProduct().getSlug())
                .productImage(primaryImage)
                .createdAt(item.getCreatedAt())
                .build();
    }
}