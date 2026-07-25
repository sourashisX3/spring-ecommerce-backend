package com.example.ecommerce_backend.modules.discount.controller;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.modules.discount.dto.request.DiscountTypeRequest;
import com.example.ecommerce_backend.modules.discount.dto.response.DiscountTypeResponse;
import com.example.ecommerce_backend.modules.discount.service.DiscountTypeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discount-types")
public class DiscountTypeController {

    @Autowired
    private DiscountTypeService discountTypeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DiscountTypeResponse>>> getAll() {
        List<DiscountTypeResponse> types = discountTypeService.getAll();
        return ApiResponse.success(types, "Discount types retrieved successfully");
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponse<DiscountTypeResponse>> getByUuid(@PathVariable String uuid) {
        DiscountTypeResponse type = discountTypeService.getByUuid(uuid);
        return ApiResponse.success(type, "Discount type retrieved successfully");
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<DiscountTypeResponse>> getByCode(@PathVariable String code) {
        DiscountTypeResponse type = discountTypeService.getByCode(code);
        return ApiResponse.success(type, "Discount type retrieved successfully");
    }

    @PostMapping
    @RequiresPermission("discount:write")
    public ResponseEntity<ApiResponse<DiscountTypeResponse>> create(@Valid @RequestBody DiscountTypeRequest request) {
        DiscountTypeResponse type = discountTypeService.create(request);
        return ApiResponse.created(type, "Discount type created successfully");
    }

    @PutMapping("/{uuid}")
    @RequiresPermission("discount:write")
    public ResponseEntity<ApiResponse<DiscountTypeResponse>> update(
            @PathVariable String uuid, @Valid @RequestBody DiscountTypeRequest request) {
        DiscountTypeResponse type = discountTypeService.update(uuid, request);
        return ApiResponse.success(type, "Discount type updated successfully");
    }

    @PatchMapping("/{uuid}/status")
    @RequiresPermission("discount:write")
    public ResponseEntity<ApiResponse<Void>> toggleStatus(
            @PathVariable String uuid, @Valid @RequestBody com.example.ecommerce_backend.core.dto.StatusRequest request) {
        discountTypeService.toggleStatus(uuid, request.isActive());
        return ApiResponse.success(null, "Discount type status updated successfully");
    }

    @DeleteMapping("/{uuid}")
    @RequiresPermission("discount:write")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String uuid) {
        discountTypeService.delete(uuid);
        return ApiResponse.success(null, "Discount type deleted successfully");
    }
}
