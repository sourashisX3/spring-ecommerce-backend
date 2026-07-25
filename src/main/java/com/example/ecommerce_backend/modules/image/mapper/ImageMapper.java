package com.example.ecommerce_backend.modules.image.mapper;

import com.example.ecommerce_backend.modules.image.dto.response.ImageResponse;
import com.example.ecommerce_backend.modules.image.entity.ProductImage;

public class ImageMapper {

    private ImageMapper() {
    }

    public static ImageResponse toImageResponse(ProductImage image) {
        return ImageResponse.builder()
                .id(image.getId())
                .uuid(image.getUuid())
                .imageUrl(image.getImageUrl())
                .isPrimary(image.isPrimary())
                .sortOrder(image.getSortOrder())
                .build();
    }
}
