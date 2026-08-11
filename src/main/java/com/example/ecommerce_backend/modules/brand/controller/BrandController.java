package com.example.ecommerce_backend.modules.brand.controller;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.core.dto.Pagination;
import com.example.ecommerce_backend.modules.brand.dto.request.BrandRequest;
import com.example.ecommerce_backend.modules.brand.dto.response.BrandResponse;
import com.example.ecommerce_backend.modules.brand.service.BrandService;
import com.example.ecommerce_backend.core.dto.StatusRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brands")
@Tag(name = "Brand", description = "Brand API")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @GetMapping
    @Operation(summary = "Get all brands", description = "Retrieves brands with optional search, active filter, sorting and pagination")
    public ResponseEntity<ApiResponse<List<BrandResponse>>> getAll(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDir,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        if (sortBy != null) {
            Sort.Direction direction = "desc".equalsIgnoreCase(sortDir)
                    ? Sort.Direction.DESC : Sort.Direction.ASC;
            sort = Sort.by(direction, sortBy);
        }

        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<BrandResponse> brands = brandService.getAll(search, active, pageable);
            return ApiResponse.paginated(
                    brands.getContent(),
                    "Brands retrieved successfully",
                    Pagination.of(brands)
            );
        }
        List<BrandResponse> brands = brandService.getAll(active);
        return ApiResponse.success(brands, "Brands retrieved successfully");
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Get brand by slug", description = "Retrieves a brand by its unique slug")
    public ResponseEntity<ApiResponse<BrandResponse>> getBySlug(@PathVariable String slug) {
        BrandResponse brand = brandService.getBySlug(slug);
        return ApiResponse.success(brand, "Brand retrieved successfully");
    }

    @PostMapping
    @RequiresPermission("brand:write")
    @Operation(summary = "Create a brand", description = "Creates a new brand with the provided details")
    public ResponseEntity<ApiResponse<BrandResponse>> create(@Valid @RequestBody BrandRequest request) {
        BrandResponse brand = brandService.create(request);
        return ApiResponse.created(brand, "Brand created successfully");
    }

    @PutMapping("/{uuid}")
    @RequiresPermission("brand:write")
    @Operation(summary = "Update a brand", description = "Updates an existing brand identified by UUID with the provided details")
    public ResponseEntity<ApiResponse<BrandResponse>> update(
            @PathVariable String uuid,
            @Valid @RequestBody BrandRequest request
    ) {
        BrandResponse brand = brandService.update(uuid, request);
        return ApiResponse.success(brand, "Brand updated successfully");
    }

    @PatchMapping("/{uuid}/status")
    @RequiresPermission("brand:write")
    @Operation(summary = "Toggle brand status", description = "Activates or deactivates a brand identified by UUID")
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
    @Operation(summary = "Delete a brand", description = "Deletes a brand identified by UUID")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String uuid) {
        brandService.delete(uuid);
        return ApiResponse.success(null, "Brand deleted successfully");
    }
}
