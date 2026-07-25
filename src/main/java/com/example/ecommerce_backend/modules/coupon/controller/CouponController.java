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
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CouponResponse>>> getAll(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Boolean global
    ) {
        List<CouponResponse> coupons = couponService.getAll(active, global);
        return ApiResponse.success(coupons, "Coupons retrieved successfully");
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponse<CouponResponse>> getByUuid(@PathVariable String uuid) {
        CouponResponse coupon = couponService.getByUuid(uuid);
        return ApiResponse.success(coupon, "Coupon retrieved successfully");
    }

    @PostMapping
    @RequiresPermission("coupon:write")
    public ResponseEntity<ApiResponse<CouponResponse>> create(@Valid @RequestBody CouponRequest request) {
        CouponResponse coupon = couponService.create(request);
        return ApiResponse.created(coupon, "Coupon created successfully");
    }

    @PutMapping("/{uuid}")
    @RequiresPermission("coupon:write")
    public ResponseEntity<ApiResponse<CouponResponse>> update(
            @PathVariable String uuid,
            @Valid @RequestBody CouponRequest request
    ) {
        CouponResponse coupon = couponService.update(uuid, request);
        return ApiResponse.success(coupon, "Coupon updated successfully");
    }

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

    @DeleteMapping("/{uuid}")
    @RequiresPermission("coupon:write")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String uuid) {
        couponService.delete(uuid);
        return ApiResponse.success(null, "Coupon deleted successfully");
    }

    @PostMapping("/{uuid}/assign")
    @RequiresPermission("coupon:write")
    public ResponseEntity<ApiResponse<Void>> assignToUsers(
            @PathVariable String uuid,
            @Valid @RequestBody AssignCouponRequest request
    ) {
        couponService.assignToUsers(uuid, request.getUserUuids());
        return ApiResponse.success(null, "Coupon assigned successfully");
    }

    @DeleteMapping("/{uuid}/assign/{userUuid}")
    @RequiresPermission("coupon:write")
    public ResponseEntity<ApiResponse<Void>> removeAssignment(
            @PathVariable String uuid,
            @PathVariable String userUuid
    ) {
        couponService.removeAssignment(uuid, userUuid);
        return ApiResponse.success(null, "Assignment removed successfully");
    }

    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<BigDecimal>> validateAndApply(
            @Valid @RequestBody CouponValidationRequest request
    ) {
        BigDecimal discount = couponService.validateAndApply(
                request.getCode(), request.getUserId(), request.getOrderSubtotal(), null);
        return ApiResponse.success(discount, "Coupon validated successfully");
    }
}
