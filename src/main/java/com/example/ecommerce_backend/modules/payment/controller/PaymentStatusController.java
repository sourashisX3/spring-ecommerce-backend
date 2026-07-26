package com.example.ecommerce_backend.modules.payment.controller;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.core.dto.StatusRequest;
import com.example.ecommerce_backend.modules.payment.service.PaymentStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment-statuses")
@Tag(name = "Payment Status", description = "Payment status management APIs")
public class PaymentStatusController {

    @Autowired
    private PaymentStatusService paymentStatusService;

    @PatchMapping("/{uuid}/status")
    @RequiresPermission("payment:write")
    @Operation(summary = "Toggle payment status", description = "Activates or deactivates a payment status")
    public ResponseEntity<ApiResponse<Void>> toggleStatus(
            @PathVariable String uuid,
            @Valid @RequestBody StatusRequest request
    ) {
        boolean changed = paymentStatusService.toggleStatus(uuid, request.isActive());
        String message = changed ? "Payment status updated successfully" : "Payment status is already " + (request.isActive() ? "active" : "inactive");
        return ApiResponse.success(null, message);
    }
}
