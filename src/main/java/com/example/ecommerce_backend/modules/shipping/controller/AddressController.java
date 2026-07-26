package com.example.ecommerce_backend.modules.shipping.controller;

import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.modules.shipping.dto.request.AddressRequest;
import com.example.ecommerce_backend.modules.shipping.dto.response.AddressResponse;
import com.example.ecommerce_backend.modules.shipping.service.ShippingAddressService;
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
@RequestMapping("/addresses")
@Tag(name = "Addresses", description = "Address management APIs")
public class AddressController {

    @Autowired
    private ShippingAddressService shippingAddressService;

    @GetMapping
    @Operation(summary = "Get all addresses", description = "Retrieves all addresses for the authenticated user")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAddresses(
            @AuthenticationPrincipal User user
    ) {
        List<AddressResponse> addresses = shippingAddressService.getAddresses(user.getId());
        return ApiResponse.success(addresses, "Addresses retrieved successfully");
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Get address by UUID", description = "Retrieves an address by its UUID for the authenticated user")
    public ResponseEntity<ApiResponse<AddressResponse>> getByUuid(
            @PathVariable String uuid,
            @AuthenticationPrincipal User user
    ) {
        AddressResponse address = shippingAddressService.getByUuid(uuid, user.getId());
        return ApiResponse.success(address, "Address retrieved successfully");
    }

    @PostMapping
    @Operation(summary = "Create address", description = "Creates a new address for the authenticated user")
    public ResponseEntity<ApiResponse<AddressResponse>> create(
            @Valid @RequestBody AddressRequest request,
            @AuthenticationPrincipal User user
    ) {
        AddressResponse address = shippingAddressService.create(request, user.getId());
        return ApiResponse.created(address, "Address created successfully");
    }

    @PutMapping("/{uuid}")
    @Operation(summary = "Update address", description = "Updates an existing address for the authenticated user")
    public ResponseEntity<ApiResponse<AddressResponse>> update(
            @PathVariable String uuid,
            @Valid @RequestBody AddressRequest request,
            @AuthenticationPrincipal User user
    ) {
        AddressResponse address = shippingAddressService.update(uuid, request, user.getId());
        return ApiResponse.success(address, "Address updated successfully");
    }

    @PatchMapping("/{uuid}/default")
    @Operation(summary = "Set default address", description = "Sets an address as the default address for the authenticated user")
    public ResponseEntity<ApiResponse<Void>> setDefault(
            @PathVariable String uuid,
            @AuthenticationPrincipal User user
    ) {
        shippingAddressService.setDefault(uuid, user.getId());
        return ApiResponse.success(null, "Default address updated successfully");
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Delete address", description = "Deletes an address for the authenticated user")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable String uuid,
            @AuthenticationPrincipal User user
    ) {
        shippingAddressService.delete(uuid, user.getId());
        return ApiResponse.success(null, "Address deleted successfully");
    }
}
