package com.example.ecommerce_backend.modules.product.controller;

import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.modules.product.dto.request.VariantRequest;
import com.example.ecommerce_backend.modules.product.dto.response.VariantResponse;
import com.example.ecommerce_backend.modules.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class VariantController {

    @Autowired
    private ProductService productService;

    @GetMapping("/products/{productUuid}/variants")
    public ResponseEntity<ApiResponse<List<VariantResponse>>> getVariants(
            @PathVariable String productUuid
    ) {
        List<VariantResponse> variants = productService.getVariants(productUuid);
        return ApiResponse.success(variants, "Variants retrieved successfully");
    }

    @GetMapping("/variants/{variantUuid}")
    public ResponseEntity<ApiResponse<VariantResponse>> getVariant(
            @PathVariable String variantUuid
    ) {
        VariantResponse variant = productService.getVariant(variantUuid);
        return ApiResponse.success(variant, "Variant retrieved successfully");
    }

    @PostMapping("/products/{productUuid}/variants")
    public ResponseEntity<ApiResponse<VariantResponse>> addVariant(
            @PathVariable String productUuid,
            @RequestBody VariantRequest request
    ) {
        VariantResponse variant = productService.addVariant(productUuid, request);
        return ApiResponse.created(variant, "Variant added successfully");
    }

    @PutMapping("/variants/{variantUuid}")
    public ResponseEntity<ApiResponse<VariantResponse>> updateVariant(
            @PathVariable String variantUuid,
            @RequestBody VariantRequest request
    ) {
        VariantResponse variant = productService.updateVariant(variantUuid, request);
        return ApiResponse.success(variant, "Variant updated successfully");
    }

    @DeleteMapping("/variants/{variantUuid}")
    public ResponseEntity<ApiResponse<Void>> deleteVariant(@PathVariable String variantUuid) {
        productService.deleteVariant(variantUuid);
        return ApiResponse.success(null, "Variant deleted successfully");
    }
}
