package com.example.ecommerce_backend.modules.product.controller;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.modules.product.dto.request.BrandRequest;
import com.example.ecommerce_backend.modules.product.dto.response.BrandResponse;
import com.example.ecommerce_backend.modules.product.service.BrandService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brands")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @GetMapping
    @RequiresPermission("brand:read")
    public ResponseEntity<ApiResponse<List<BrandResponse>>> getAll() {
        List<BrandResponse> brands = brandService.getAll();
        return ApiResponse.success(brands, "Brands retrieved successfully");
    }

    @GetMapping("/{slug}")
    @RequiresPermission("brand:read")
    public ResponseEntity<ApiResponse<BrandResponse>> getBySlug(@PathVariable String slug) {
        BrandResponse brand = brandService.getBySlug(slug);
        return ApiResponse.success(brand, "Brand retrieved successfully");
    }

    @PostMapping
    @RequiresPermission("brand:write")
    public ResponseEntity<ApiResponse<BrandResponse>> create(@Valid @RequestBody BrandRequest request) {
        BrandResponse brand = brandService.create(request);
        return ApiResponse.created(brand, "Brand created successfully");
    }

    @PutMapping("/{slug}")
    @RequiresPermission("brand:write")
    public ResponseEntity<ApiResponse<BrandResponse>> update(
            @PathVariable String slug,
            @Valid @RequestBody BrandRequest request
    ) {
        BrandResponse brand = brandService.update(slug, request);
        return ApiResponse.success(brand, "Brand updated successfully");
    }

    @DeleteMapping("/{slug}")
    @RequiresPermission("brand:write")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String slug) {
        brandService.delete(slug);
        return ApiResponse.success(null, "Brand deleted successfully");
    }
}
