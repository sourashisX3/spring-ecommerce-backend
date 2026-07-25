package com.example.ecommerce_backend.modules.shipping.controller;

import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.modules.shipping.dto.request.AddressRequest;
import com.example.ecommerce_backend.modules.shipping.dto.response.AddressResponse;
import com.example.ecommerce_backend.modules.shipping.service.ShippingAddressService;
import com.example.ecommerce_backend.modules.user.entity.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    @Autowired
    private ShippingAddressService shippingAddressService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAddresses(
            @AuthenticationPrincipal User user
    ) {
        List<AddressResponse> addresses = shippingAddressService.getAddresses(user.getId());
        return ApiResponse.success(addresses, "Addresses retrieved successfully");
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponse<AddressResponse>> getByUuid(
            @PathVariable String uuid,
            @AuthenticationPrincipal User user
    ) {
        AddressResponse address = shippingAddressService.getByUuid(uuid, user.getId());
        return ApiResponse.success(address, "Address retrieved successfully");
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> create(
            @Valid @RequestBody AddressRequest request,
            @AuthenticationPrincipal User user
    ) {
        AddressResponse address = shippingAddressService.create(request, user.getId());
        return ApiResponse.created(address, "Address created successfully");
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<ApiResponse<AddressResponse>> update(
            @PathVariable String uuid,
            @Valid @RequestBody AddressRequest request,
            @AuthenticationPrincipal User user
    ) {
        AddressResponse address = shippingAddressService.update(uuid, request, user.getId());
        return ApiResponse.success(address, "Address updated successfully");
    }

    @PatchMapping("/{uuid}/default")
    public ResponseEntity<ApiResponse<Void>> setDefault(
            @PathVariable String uuid,
            @AuthenticationPrincipal User user
    ) {
        shippingAddressService.setDefault(uuid, user.getId());
        return ApiResponse.success(null, "Default address updated successfully");
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable String uuid,
            @AuthenticationPrincipal User user
    ) {
        shippingAddressService.delete(uuid, user.getId());
        return ApiResponse.success(null, "Address deleted successfully");
    }
}
