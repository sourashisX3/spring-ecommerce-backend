package com.example.ecommerce_backend.modules.discount.controller;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.core.dto.StatusRequest;
import com.example.ecommerce_backend.modules.discount.dto.request.DiscountRequest;
import com.example.ecommerce_backend.modules.discount.dto.response.DiscountResponse;
import com.example.ecommerce_backend.modules.discount.service.DiscountService;
import com.example.ecommerce_backend.modules.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/discounts")
@Tag(name = "Discount", description = "Discount API")
public class DiscountController {

    @Autowired
    private DiscountService discountService;

    @Operation(summary = "Get all discounts", description = "Retrieves all discounts with optional filtering by active and global status and search")
    @GetMapping
    public ResponseEntity<ApiResponse<List<DiscountResponse>>> getAll(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Boolean global,
            @RequestParam(required = false) String search) {
        List<DiscountResponse> discounts = discountService.getAll(active, global, search);
        return ApiResponse.success(discounts, "Discounts retrieved successfully");
    }

    @Operation(summary = "Get discount by UUID", description = "Retrieves a discount by its unique identifier")
    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponse<DiscountResponse>> getByUuid(@PathVariable String uuid) {
        DiscountResponse discount = discountService.getByUuid(uuid);
        return ApiResponse.success(discount, "Discount retrieved successfully");
    }

    @Operation(summary = "Get eligible discounts", description = "Retrieves discounts eligible for the authenticated user")
    @GetMapping("/eligible")
    public ResponseEntity<ApiResponse<List<DiscountResponse>>> getEligible(
            @AuthenticationPrincipal User user) {
        List<DiscountResponse> discounts = discountService.getEligibleDiscounts(user.getId());
        return ApiResponse.success(discounts, "Eligible discounts retrieved successfully");
    }

    @Operation(summary = "Create a discount", description = "Creates a new discount (requires discount:write permission)")
    @PostMapping
    @RequiresPermission("discount:write")
    public ResponseEntity<ApiResponse<DiscountResponse>> create(@Valid @RequestBody DiscountRequest request) {
        DiscountResponse discount;
        if (request.getUserUuids() != null && !request.getUserUuids().isEmpty()) {
            discount = discountService.createAssignable(request, request.getUserUuids());
        } else {
            discount = discountService.create(request);
        }
        return ApiResponse.created(discount, "Discount created successfully");
    }

    @Operation(summary = "Update a discount", description = "Updates an existing discount by UUID (requires discount:write permission)")
    @PutMapping("/{uuid}")
    @RequiresPermission("discount:write")
    public ResponseEntity<ApiResponse<DiscountResponse>> update(
            @PathVariable String uuid, @Valid @RequestBody DiscountRequest request) {
        DiscountResponse discount = discountService.update(uuid, request);
        return ApiResponse.success(discount, "Discount updated successfully");
    }

    @Operation(summary = "Toggle discount status", description = "Activates or deactivates a discount (requires discount:write permission)")
    @PatchMapping("/{uuid}/status")
    @RequiresPermission("discount:write")
    public ResponseEntity<ApiResponse<Void>> toggleStatus(
            @PathVariable String uuid, @Valid @RequestBody StatusRequest request) {
        discountService.toggleStatus(uuid, request.isActive());
        return ApiResponse.success(null, "Discount status updated successfully");
    }

    @Operation(summary = "Delete a discount", description = "Deletes a discount by UUID (requires discount:write permission)")
    @DeleteMapping("/{uuid}")
    @RequiresPermission("discount:write")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String uuid) {
        discountService.delete(uuid);
        return ApiResponse.success(null, "Discount deleted successfully");
    }

    @Operation(summary = "Assign discount to users", description = "Assigns a discount to specific users (requires discount:write permission)")
    @PostMapping("/{uuid}/assign")
    @RequiresPermission("discount:write")
    public ResponseEntity<ApiResponse<Void>> assignToUsers(
            @PathVariable String uuid, @Valid @RequestBody List<String> userUuids) {
        discountService.assignToUsers(uuid, userUuids);
        return ApiResponse.success(null, "Discount assigned to users successfully");
    }

    @Operation(summary = "Remove discount assignment", description = "Removes a discount assignment from a user (requires discount:write permission)")
    @DeleteMapping("/{uuid}/assign/{userUuid}")
    @RequiresPermission("discount:write")
    public ResponseEntity<ApiResponse<Void>> removeAssignment(
            @PathVariable String uuid, @PathVariable String userUuid) {
        discountService.removeAssignment(uuid, userUuid);
        return ApiResponse.success(null, "Assignment removed successfully");
    }
}
