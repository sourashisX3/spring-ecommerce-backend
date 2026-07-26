package com.example.ecommerce_backend.modules.discount.controller;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.modules.discount.dto.request.DiscountTypeRequest;
import com.example.ecommerce_backend.modules.discount.dto.response.DiscountTypeResponse;
import com.example.ecommerce_backend.modules.discount.service.DiscountTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/discount-types")
@Tag(name = "Discount Type", description = "Discount Type API")
public class DiscountTypeController {

    @Autowired
    private DiscountTypeService discountTypeService;

    @Operation(summary = "Get all discount types", description = "Retrieves all discount types")
    @GetMapping
    public ResponseEntity<ApiResponse<List<DiscountTypeResponse>>> getAll() {
        List<DiscountTypeResponse> types = discountTypeService.getAll();
        return ApiResponse.success(types, "Discount types retrieved successfully");
    }

    @Operation(summary = "Get discount type by UUID", description = "Retrieves a discount type by its unique identifier")
    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponse<DiscountTypeResponse>> getByUuid(@PathVariable String uuid) {
        DiscountTypeResponse type = discountTypeService.getByUuid(uuid);
        return ApiResponse.success(type, "Discount type retrieved successfully");
    }

    @Operation(summary = "Get discount type by code", description = "Retrieves a discount type by its code")
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<DiscountTypeResponse>> getByCode(@PathVariable String code) {
        DiscountTypeResponse type = discountTypeService.getByCode(code);
        return ApiResponse.success(type, "Discount type retrieved successfully");
    }

    @Operation(summary = "Create a discount type", description = "Creates a new discount type (requires discount:write permission)")
    @PostMapping
    @RequiresPermission("discount:write")
    public ResponseEntity<ApiResponse<DiscountTypeResponse>> create(@Valid @RequestBody DiscountTypeRequest request) {
        DiscountTypeResponse type = discountTypeService.create(request);
        return ApiResponse.created(type, "Discount type created successfully");
    }

    @Operation(summary = "Update a discount type", description = "Updates an existing discount type by UUID (requires discount:write permission)")
    @PutMapping("/{uuid}")
    @RequiresPermission("discount:write")
    public ResponseEntity<ApiResponse<DiscountTypeResponse>> update(
            @PathVariable String uuid, @Valid @RequestBody DiscountTypeRequest request) {
        DiscountTypeResponse type = discountTypeService.update(uuid, request);
        return ApiResponse.success(type, "Discount type updated successfully");
    }

    @Operation(summary = "Toggle discount type status", description = "Activates or deactivates a discount type (requires discount:write permission)")
    @PatchMapping("/{uuid}/status")
    @RequiresPermission("discount:write")
    public ResponseEntity<ApiResponse<Void>> toggleStatus(
            @PathVariable String uuid, @Valid @RequestBody com.example.ecommerce_backend.core.dto.StatusRequest request) {
        discountTypeService.toggleStatus(uuid, request.isActive());
        return ApiResponse.success(null, "Discount type status updated successfully");
    }

    @Operation(summary = "Delete a discount type", description = "Deletes a discount type by UUID (requires discount:write permission)")
    @DeleteMapping("/{uuid}")
    @RequiresPermission("discount:write")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String uuid) {
        discountTypeService.delete(uuid);
        return ApiResponse.success(null, "Discount type deleted successfully");
    }
}
