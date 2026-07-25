package com.example.ecommerce_backend.modules.brand.controller;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.modules.brand.dto.request.BrandRequest;
import com.example.ecommerce_backend.modules.brand.dto.response.BrandResponse;
import com.example.ecommerce_backend.modules.brand.service.BrandService;
import com.example.ecommerce_backend.core.dto.StatusRequest;
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
    public ResponseEntity<ApiResponse<List<BrandResponse>>> getAll(
            @RequestParam(required = false) Boolean active
    ) {
        List<BrandResponse> brands = brandService.getAll(active);
        return ApiResponse.success(brands, "Brands retrieved successfully");
    }

    @GetMapping("/{slug}")
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

    @PutMapping("/{uuid}")
    @RequiresPermission("brand:write")
    public ResponseEntity<ApiResponse<BrandResponse>> update(
            @PathVariable String uuid,
            @Valid @RequestBody BrandRequest request
    ) {
        BrandResponse brand = brandService.update(uuid, request);
        return ApiResponse.success(brand, "Brand updated successfully");
    }

    @PatchMapping("/{uuid}/status")
    @RequiresPermission("brand:write")
    public ResponseEntity<ApiResponse<Void>> toggleStatus(
            @PathVariable String uuid,
            @Valid @RequestBody StatusRequest request
    ) {
        boolean changed = brandService.toggleStatus(uuid, request.isActive());
        String message = changed ? "Brand status updated successfully" : "Brand is already " + (request.isActive() ? "active" : "inactive");
        return ApiResponse.success(null, message);
    }

    @DeleteMapping("/{uuid}")
    @RequiresPermission("brand:write")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String uuid) {
        brandService.delete(uuid);
        return ApiResponse.success(null, "Brand deleted successfully");
    }
}
