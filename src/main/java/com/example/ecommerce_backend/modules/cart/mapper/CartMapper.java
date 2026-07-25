package com.example.ecommerce_backend.modules.cart.mapper;

import com.example.ecommerce_backend.modules.cart.dto.response.CartItemResponse;
import com.example.ecommerce_backend.modules.cart.entity.CartItem;
import com.example.ecommerce_backend.modules.image.entity.ProductImage;

public class CartMapper {

    private CartMapper() {
    }

    public static CartItemResponse toResponse(CartItem item) {
        String primaryImage = item.getProduct().getImages() != null
                ? item.getProduct().getImages().stream()
                        .filter(ProductImage::isPrimary)
                        .findFirst()
                        .map(ProductImage::getImageUrl)
                        .orElse(null)
                : null;

        return CartItemResponse.builder()
                .uuid(item.getUuid())
                .productUuid(item.getProduct().getUuid())
                .productName(item.getProduct().getName())
                .productSlug(item.getProduct().getSlug())
                .productImage(primaryImage)
                .productPrice(item.getProduct().getBasePrice())
                .quantity(item.getQuantity())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
}
