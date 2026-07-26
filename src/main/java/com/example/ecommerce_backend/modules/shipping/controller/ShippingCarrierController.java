package com.example.ecommerce_backend.modules.shipping.controller;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.modules.shipping.dto.request.ShippingCarrierRequest;
import com.example.ecommerce_backend.modules.shipping.dto.response.ShippingCarrierResponse;
import com.example.ecommerce_backend.modules.shipping.service.ShippingCarrierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shipping-carriers")
@Tag(name = "Shipping Carriers", description = "Shipping carrier management APIs")
public class ShippingCarrierController {

    @Autowired
    private ShippingCarrierService shippingCarrierService;

    @GetMapping
    @Operation(summary = "Get all shipping carriers", description = "Retrieves all shipping carriers")
    public ResponseEntity<ApiResponse<List<ShippingCarrierResponse>>> getAll() {
        List<ShippingCarrierResponse> carriers = shippingCarrierService.getAll();
        return ApiResponse.success(carriers, "Shipping carriers retrieved successfully");
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Get shipping carrier by UUID", description = "Retrieves a shipping carrier by its UUID")
    public ResponseEntity<ApiResponse<ShippingCarrierResponse>> getByUuid(@PathVariable String uuid) {
        ShippingCarrierResponse carrier = shippingCarrierService.getByUuid(uuid);
        return ApiResponse.success(carrier, "Shipping carrier retrieved successfully");
    }

    @PostMapping
    @RequiresPermission("shipping:write")
    @Operation(summary = "Create shipping carrier", description = "Creates a new shipping carrier")
    public ResponseEntity<ApiResponse<ShippingCarrierResponse>> create(@Valid @RequestBody ShippingCarrierRequest request) {
        ShippingCarrierResponse carrier = shippingCarrierService.create(request);
        return ApiResponse.created(carrier, "Shipping carrier created successfully");
    }

    @PutMapping("/{uuid}")
    @RequiresPermission("shipping:write")
    @Operation(summary = "Update shipping carrier", description = "Updates an existing shipping carrier")
    public ResponseEntity<ApiResponse<ShippingCarrierResponse>> update(
            @PathVariable String uuid,
            @Valid @RequestBody ShippingCarrierRequest request
    ) {
        ShippingCarrierResponse carrier = shippingCarrierService.update(uuid, request);
        return ApiResponse.success(carrier, "Shipping carrier updated successfully");
    }

    @PatchMapping("/{uuid}/status")
    @RequiresPermission("shipping:write")
    @Operation(summary = "Toggle shipping carrier status", description = "Toggles the active status of a shipping carrier")
    public ResponseEntity<ApiResponse<ShippingCarrierResponse>> toggleStatus(@PathVariable String uuid) {
        ShippingCarrierResponse carrier = shippingCarrierService.toggleStatus(uuid);
        return ApiResponse.success(carrier, "Shipping carrier status toggled successfully");
    }

    @DeleteMapping("/{uuid}")
    @RequiresPermission("shipping:write")
    @Operation(summary = "Delete shipping carrier", description = "Deletes a shipping carrier")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String uuid) {
        shippingCarrierService.delete(uuid);
        return ApiResponse.success(null, "Shipping carrier deleted successfully");
    }
}
