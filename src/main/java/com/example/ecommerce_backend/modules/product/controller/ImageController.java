package com.example.ecommerce_backend.modules.product.controller;

import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.modules.product.dto.request.ImageRequest;
import com.example.ecommerce_backend.modules.product.dto.response.ImageResponse;
import com.example.ecommerce_backend.modules.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ImageController {

    @Autowired
    private ProductService productService;

    @PostMapping("/products/{productUuid}/images")
    public ResponseEntity<ApiResponse<ImageResponse>> addImage(
            @PathVariable String productUuid,
            @RequestBody ImageRequest request
    ) {
        ImageResponse image = productService.addImage(productUuid, request);
        return ApiResponse.created(image, "Image added successfully");
    }

    @DeleteMapping("/images/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteImage(@PathVariable Long id) {
        productService.deleteImage(id);
        return ApiResponse.success(null, "Image deleted successfully");
    }
}
