package com.example.ecommerce_backend.modules.payment.controller;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.core.dto.StatusRequest;
import com.example.ecommerce_backend.modules.payment.dto.request.PaymentGatewayRequest;
import com.example.ecommerce_backend.modules.payment.dto.response.PaymentGatewayResponse;
import com.example.ecommerce_backend.modules.payment.service.PaymentGatewayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment-gateways")
@Tag(name = "Payment Gateway", description = "Payment Gateway API")
public class PaymentGatewayController {

    @Autowired
    private PaymentGatewayService paymentGatewayService;

    @GetMapping
    @Operation(summary = "Get all payment gateways", description = "Retrieve all payment gateways")
    public ResponseEntity<ApiResponse<List<PaymentGatewayResponse>>> getAll() {
        List<PaymentGatewayResponse> gateways = paymentGatewayService.getAll();
        return ApiResponse.success(gateways, "Payment gateways retrieved successfully");
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Get payment gateway by UUID", description = "Retrieve a payment gateway by UUID")
    public ResponseEntity<ApiResponse<PaymentGatewayResponse>> getByUuid(@PathVariable String uuid) {
        PaymentGatewayResponse gateway = paymentGatewayService.getByUuid(uuid);
        return ApiResponse.success(gateway, "Payment gateway retrieved successfully");
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get payment gateway by code", description = "Retrieve a payment gateway by its code")
    public ResponseEntity<ApiResponse<PaymentGatewayResponse>> getByCode(@PathVariable String code) {
        PaymentGatewayResponse gateway = paymentGatewayService.getByCode(code);
        return ApiResponse.success(gateway, "Payment gateway retrieved successfully");
    }

    @PostMapping
    @RequiresPermission("payment:write")
    @Operation(summary = "Create payment gateway", description = "Create a new payment gateway")
    public ResponseEntity<ApiResponse<PaymentGatewayResponse>> create(@Valid @RequestBody PaymentGatewayRequest request) {
        PaymentGatewayResponse gateway = paymentGatewayService.create(request);
        return ApiResponse.created(gateway, "Payment gateway created successfully");
    }

    @PutMapping("/{uuid}")
    @RequiresPermission("payment:write")
    @Operation(summary = "Update payment gateway", description = "Update an existing payment gateway")
    public ResponseEntity<ApiResponse<PaymentGatewayResponse>> update(
            @PathVariable String uuid, @Valid @RequestBody PaymentGatewayRequest request) {
        PaymentGatewayResponse gateway = paymentGatewayService.update(uuid, request);
        return ApiResponse.success(gateway, "Payment gateway updated successfully");
    }

    @PatchMapping("/{uuid}/status")
    @RequiresPermission("payment:write")
    @Operation(summary = "Toggle payment gateway status", description = "Activate or deactivate a payment gateway")
    public ResponseEntity<ApiResponse<Void>> toggleStatus(
            @PathVariable String uuid, @Valid @RequestBody StatusRequest request) {
        paymentGatewayService.toggleStatus(uuid, request.isActive());
        return ApiResponse.success(null, "Payment gateway status updated successfully");
    }

    @DeleteMapping("/{uuid}")
    @RequiresPermission("payment:write")
    @Operation(summary = "Delete payment gateway", description = "Delete a payment gateway by UUID")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String uuid) {
        paymentGatewayService.delete(uuid);
        return ApiResponse.success(null, "Payment gateway deleted successfully");
    }
}
