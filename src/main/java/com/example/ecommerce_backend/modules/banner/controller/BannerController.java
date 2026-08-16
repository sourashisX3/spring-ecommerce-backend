package com.example.ecommerce_backend.modules.banner.controller;

import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.core.dto.StatusRequest;
import com.example.ecommerce_backend.modules.banner.dto.request.BannerRequest;
import com.example.ecommerce_backend.modules.banner.dto.response.BannerResponse;
import com.example.ecommerce_backend.modules.banner.service.BannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/banners")
@Tag(name = "Banner", description = "Banner API")
public class BannerController {

    @Autowired
    private BannerService bannerService;

    @GetMapping
    @Operation(summary = "Get all banners", description = "Retrieve all banners with optional active filter")
    public ResponseEntity<ApiResponse<List<BannerResponse>>> getAll(
            @RequestParam(required = false) Boolean active
    ) {
        List<BannerResponse> banners = bannerService.getAll(active);
        return ApiResponse.success(banners, "Banners retrieved successfully");
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Get banner by UUID", description = "Retrieve a banner by its UUID")
    public ResponseEntity<ApiResponse<BannerResponse>> getByUuid(@PathVariable String uuid) {
        BannerResponse banner = bannerService.getByUuid(uuid);
        return ApiResponse.success(banner, "Banner retrieved successfully");
    }

    @PostMapping
    @Operation(summary = "Create banner", description = "Create a new banner")
    public ResponseEntity<ApiResponse<BannerResponse>> create(@Valid @RequestBody BannerRequest request) {
        BannerResponse banner = bannerService.create(request);
        return ApiResponse.created(banner, "Banner created successfully");
    }

    @PutMapping("/{uuid}")
    @Operation(summary = "Update banner", description = "Update an existing banner by UUID")
    public ResponseEntity<ApiResponse<BannerResponse>> update(
            @PathVariable String uuid,
            @Valid @RequestBody BannerRequest request
    ) {
        BannerResponse banner = bannerService.update(uuid, request);
        return ApiResponse.success(banner, "Banner updated successfully");
    }

    @PatchMapping("/{uuid}/status")
    @Operation(summary = "Toggle banner status", description = "Activate or deactivate a banner")
    public ResponseEntity<ApiResponse<Void>> toggleStatus(
            @PathVariable String uuid,
            @Valid @RequestBody StatusRequest request
    ) {
        bannerService.toggleStatus(uuid, request.isActive());
        String message = request.isActive() ? "Banner activated successfully" : "Banner deactivated successfully";
        return ApiResponse.success(null, message);
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Delete banner", description = "Delete a banner by UUID")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String uuid) {
        bannerService.delete(uuid);
        return ApiResponse.success(null, "Banner deleted successfully");
    }
}