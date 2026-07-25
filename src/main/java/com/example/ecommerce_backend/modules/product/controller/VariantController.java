package com.example.ecommerce_backend.modules.product.controller;

import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.modules.product.dto.request.VariantRequest;
import com.example.ecommerce_backend.modules.product.dto.response.VariantResponse;
import com.example.ecommerce_backend.modules.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class VariantController {

    @Autowired
    private ProductService productService;

    @PostMapping("/products/{productUuid}/variants")
    public ResponseEntity<ApiResponse<VariantResponse>> addVariant(
            @PathVariable String productUuid,
            @RequestBody VariantRequest request
    ) {
        VariantResponse variant = productService.addVariant(productUuid, request);
        return ApiResponse.created(variant, "Variant added successfully");
    }

    @PutMapping("/variants/{id}")
    public ResponseEntity<ApiResponse<VariantResponse>> updateVariant(
            @PathVariable Long id,
            @RequestBody VariantRequest request
    ) {
        VariantResponse variant = productService.updateVariant(id, request);
        return ApiResponse.success(variant, "Variant updated successfully");
    }

    @DeleteMapping("/variants/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVariant(@PathVariable Long id) {
        productService.deleteVariant(id);
        return ApiResponse.success(null, "Variant deleted successfully");
    }
}
