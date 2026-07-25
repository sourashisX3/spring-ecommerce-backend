package com.example.ecommerce_backend.modules.shipping.controller;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.modules.shipping.dto.request.ShippingCarrierRequest;
import com.example.ecommerce_backend.modules.shipping.dto.response.ShippingCarrierResponse;
import com.example.ecommerce_backend.modules.shipping.service.ShippingCarrierService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipping-carriers")
public class ShippingCarrierController {

    @Autowired
    private ShippingCarrierService shippingCarrierService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ShippingCarrierResponse>>> getAll() {
        List<ShippingCarrierResponse> carriers = shippingCarrierService.getAll();
        return ApiResponse.success(carriers, "Shipping carriers retrieved successfully");
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponse<ShippingCarrierResponse>> getByUuid(@PathVariable String uuid) {
        ShippingCarrierResponse carrier = shippingCarrierService.getByUuid(uuid);
        return ApiResponse.success(carrier, "Shipping carrier retrieved successfully");
    }

    @PostMapping
    @RequiresPermission("shipping:write")
    public ResponseEntity<ApiResponse<ShippingCarrierResponse>> create(@Valid @RequestBody ShippingCarrierRequest request) {
        ShippingCarrierResponse carrier = shippingCarrierService.create(request);
        return ApiResponse.created(carrier, "Shipping carrier created successfully");
    }

    @PutMapping("/{uuid}")
    @RequiresPermission("shipping:write")
    public ResponseEntity<ApiResponse<ShippingCarrierResponse>> update(
            @PathVariable String uuid,
            @Valid @RequestBody ShippingCarrierRequest request
    ) {
        ShippingCarrierResponse carrier = shippingCarrierService.update(uuid, request);
        return ApiResponse.success(carrier, "Shipping carrier updated successfully");
    }

    @PatchMapping("/{uuid}/status")
    @RequiresPermission("shipping:write")
    public ResponseEntity<ApiResponse<ShippingCarrierResponse>> toggleStatus(@PathVariable String uuid) {
        ShippingCarrierResponse carrier = shippingCarrierService.toggleStatus(uuid);
        return ApiResponse.success(carrier, "Shipping carrier status toggled successfully");
    }

    @DeleteMapping("/{uuid}")
    @RequiresPermission("shipping:write")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String uuid) {
        shippingCarrierService.delete(uuid);
        return ApiResponse.success(null, "Shipping carrier deleted successfully");
    }
}
