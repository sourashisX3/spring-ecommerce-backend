package com.example.ecommerce_backend.modules.image.controller;

import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.modules.image.dto.request.ImageRequest;
import com.example.ecommerce_backend.modules.image.dto.response.ImageResponse;
import com.example.ecommerce_backend.modules.image.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Image", description = "Image API")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @Operation(summary = "Add image to product", description = "Adds an image to a product by product UUID")
    @PostMapping("/products/{productUuid}/images")
    public ResponseEntity<ApiResponse<ImageResponse>> addImage(
            @PathVariable String productUuid,
            @Valid @RequestBody ImageRequest request
    ) {
        ImageResponse image = imageService.addImage(productUuid, request);
        return ApiResponse.created(image, "Image added successfully");
    }

    @Operation(summary = "Delete an image", description = "Deletes an image by its UUID")
    @DeleteMapping("/images/{imageUuid}")
    public ResponseEntity<ApiResponse<Void>> deleteImage(@PathVariable String imageUuid) {
        imageService.deleteImage(imageUuid);
        return ApiResponse.success(null, "Image deleted successfully");
    }
}
