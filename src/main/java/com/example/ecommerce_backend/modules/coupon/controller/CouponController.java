package com.example.ecommerce_backend.modules.coupon.controller;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.core.dto.Pagination;
import com.example.ecommerce_backend.core.dto.StatusRequest;
import com.example.ecommerce_backend.modules.coupon.dto.request.AssignCouponRequest;
import com.example.ecommerce_backend.modules.coupon.dto.request.CouponRequest;
import com.example.ecommerce_backend.modules.coupon.dto.request.CouponValidationRequest;
import com.example.ecommerce_backend.modules.coupon.dto.response.CouponResponse;
import com.example.ecommerce_backend.modules.coupon.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/coupons")
@Tag(name = "Coupon", description = "Coupon API")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @Operation(summary = "Get all coupons", description = "Retrieves all coupons with optional filtering by active, global status and search")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CouponResponse>>> getAll(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Boolean global,
            @RequestParam(required = false) String search
    ) {
        List<CouponResponse> coupons = couponService.getAll(active, global, search);
        return ApiResponse.success(coupons, "Coupons retrieved successfully");
    }

    @Operation(summary = "Get coupon by UUID", description = "Retrieves a coupon by its unique identifier")
    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponse<CouponResponse>> getByUuid(@PathVariable String uuid) {
        CouponResponse coupon = couponService.getByUuid(uuid);
        return ApiResponse.success(coupon, "Coupon retrieved successfully");
    }

    @Operation(summary = "Create a coupon", description = "Creates a new coupon (requires coupon:write permission)")
    @PostMapping
    @RequiresPermission("coupon:write")
    public ResponseEntity<ApiResponse<CouponResponse>> create(@Valid @RequestBody CouponRequest request) {
        CouponResponse coupon = couponService.create(request);
        return ApiResponse.created(coupon, "Coupon created successfully");
    }

    @Operation(summary = "Update a coupon", description = "Updates an existing coupon by UUID (requires coupon:write permission)")
    @PutMapping("/{uuid}")
    @RequiresPermission("coupon:write")
    public ResponseEntity<ApiResponse<CouponResponse>> update(
            @PathVariable String uuid,
            @Valid @RequestBody CouponRequest request
    ) {
        CouponResponse coupon = couponService.update(uuid, request);
        return ApiResponse.success(coupon, "Coupon updated successfully");
    }

    @Operation(summary = "Toggle coupon status", description = "Activates or deactivates a coupon (requires coupon:write permission)")
    @PatchMapping("/{uuid}/status")
    @RequiresPermission("coupon:write")
    public ResponseEntity<ApiResponse<Void>> toggleStatus(
            @PathVariable String uuid,
            @Valid @RequestBody StatusRequest request
    ) {
        couponService.toggleStatus(uuid, request.isActive());
        String message = request.isActive() ? "Coupon activated successfully" : "Coupon deactivated successfully";
        return ApiResponse.success(null, message);
    }

    @Operation(summary = "Delete a coupon", description = "Deletes a coupon by UUID (requires coupon:write permission)")
    @DeleteMapping("/{uuid}")
    @RequiresPermission("coupon:write")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String uuid) {
        couponService.delete(uuid);
        return ApiResponse.success(null, "Coupon deleted successfully");
    }

    @Operation(summary = "Assign coupon to users", description = "Assigns a coupon to specific users (requires coupon:write permission)")
    @PostMapping("/{uuid}/assign")
    @RequiresPermission("coupon:write")
    public ResponseEntity<ApiResponse<Void>> assignToUsers(
            @PathVariable String uuid,
            @Valid @RequestBody AssignCouponRequest request
    ) {
        couponService.assignToUsers(uuid, request.getUserUuids());
        return ApiResponse.success(null, "Coupon assigned successfully");
    }

    @Operation(summary = "Remove coupon assignment", description = "Removes a coupon assignment from a user (requires coupon:write permission)")
    @DeleteMapping("/{uuid}/assign/{userUuid}")
    @RequiresPermission("coupon:write")
    public ResponseEntity<ApiResponse<Void>> removeAssignment(
            @PathVariable String uuid,
            @PathVariable String userUuid
    ) {
        couponService.removeAssignment(uuid, userUuid);
        return ApiResponse.success(null, "Assignment removed successfully");
    }

    @Operation(summary = "Validate and apply coupon", description = "Validates a coupon code and applies it to an order subtotal")
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<BigDecimal>> validateAndApply(
            @Valid @RequestBody CouponValidationRequest request
    ) {
        BigDecimal discount = couponService.validateAndApply(
                request.getCode(), request.getUserId(), request.getOrderSubtotal(), null);
        return ApiResponse.success(discount, "Coupon validated successfully");
    }
}
