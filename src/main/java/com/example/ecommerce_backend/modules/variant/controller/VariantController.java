package com.example.ecommerce_backend.modules.variant.controller;

import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.modules.variant.dto.request.VariantRequest;
import com.example.ecommerce_backend.modules.variant.dto.response.VariantResponse;
import com.example.ecommerce_backend.modules.variant.service.VariantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class VariantController {

    @Autowired
    private VariantService variantService;

    @GetMapping("/products/{productUuid}/variants")
    public ResponseEntity<ApiResponse<List<VariantResponse>>> getVariants(
            @PathVariable String productUuid
    ) {
        List<VariantResponse> variants = variantService.getVariants(productUuid);
        return ApiResponse.success(variants, "Variants retrieved successfully");
    }

    @GetMapping("/variants/{variantUuid}")
    public ResponseEntity<ApiResponse<VariantResponse>> getVariant(
            @PathVariable String variantUuid
    ) {
        VariantResponse variant = variantService.getVariant(variantUuid);
        return ApiResponse.success(variant, "Variant retrieved successfully");
    }

    @PostMapping("/products/{productUuid}/variants")
    public ResponseEntity<ApiResponse<VariantResponse>> addVariant(
            @PathVariable String productUuid,
            @Valid @RequestBody VariantRequest request
    ) {
        VariantResponse variant = variantService.addVariant(productUuid, request);
        return ApiResponse.created(variant, "Variant added successfully");
    }

    @PutMapping("/variants/{variantUuid}")
    public ResponseEntity<ApiResponse<VariantResponse>> updateVariant(
            @PathVariable String variantUuid,
            @Valid @RequestBody VariantRequest request
    ) {
        VariantResponse variant = variantService.updateVariant(variantUuid, request);
        return ApiResponse.success(variant, "Variant updated successfully");
    }

    @DeleteMapping("/variants/{variantUuid}")
    public ResponseEntity<ApiResponse<Void>> deleteVariant(@PathVariable String variantUuid) {
        variantService.deleteVariant(variantUuid);
        return ApiResponse.success(null, "Variant deleted successfully");
    }
}
