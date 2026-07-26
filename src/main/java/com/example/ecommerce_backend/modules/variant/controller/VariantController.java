package com.example.ecommerce_backend.modules.variant.controller;

import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.modules.variant.dto.request.VariantRequest;
import com.example.ecommerce_backend.modules.variant.dto.response.VariantResponse;
import com.example.ecommerce_backend.modules.variant.service.VariantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Variants", description = "Variant management APIs")
public class VariantController {

    @Autowired
    private VariantService variantService;

    @GetMapping("/products/{productUuid}/variants")
    @Operation(summary = "Get variants by product", description = "Retrieves all variants for a given product")
    public ResponseEntity<ApiResponse<List<VariantResponse>>> getVariants(
            @PathVariable String productUuid
    ) {
        List<VariantResponse> variants = variantService.getVariants(productUuid);
        return ApiResponse.success(variants, "Variants retrieved successfully");
    }

    @GetMapping("/variants/{variantUuid}")
    @Operation(summary = "Get variant by UUID", description = "Retrieves a variant by its UUID")
    public ResponseEntity<ApiResponse<VariantResponse>> getVariant(
            @PathVariable String variantUuid
    ) {
        VariantResponse variant = variantService.getVariant(variantUuid);
        return ApiResponse.success(variant, "Variant retrieved successfully");
    }

    @PostMapping("/products/{productUuid}/variants")
    @Operation(summary = "Add variant", description = "Adds a new variant to a product")
    public ResponseEntity<ApiResponse<VariantResponse>> addVariant(
            @PathVariable String productUuid,
            @Valid @RequestBody VariantRequest request
    ) {
        VariantResponse variant = variantService.addVariant(productUuid, request);
        return ApiResponse.created(variant, "Variant added successfully");
    }

    @PutMapping("/variants/{variantUuid}")
    @Operation(summary = "Update variant", description = "Updates an existing variant")
    public ResponseEntity<ApiResponse<VariantResponse>> updateVariant(
            @PathVariable String variantUuid,
            @Valid @RequestBody VariantRequest request
    ) {
        VariantResponse variant = variantService.updateVariant(variantUuid, request);
        return ApiResponse.success(variant, "Variant updated successfully");
    }

    @DeleteMapping("/variants/{variantUuid}")
    @Operation(summary = "Delete variant", description = "Deletes a variant")
    public ResponseEntity<ApiResponse<Void>> deleteVariant(@PathVariable String variantUuid) {
        variantService.deleteVariant(variantUuid);
        return ApiResponse.success(null, "Variant deleted successfully");
    }
}
