package com.example.ecommerce_backend.modules.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ImageRequest {

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    private boolean isPrimary;

    private int sortOrder;
}
