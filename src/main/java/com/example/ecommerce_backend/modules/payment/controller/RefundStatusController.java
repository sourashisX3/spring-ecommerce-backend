package com.example.ecommerce_backend.modules.payment.controller;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.core.dto.StatusRequest;
import com.example.ecommerce_backend.modules.payment.service.RefundStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/refund-statuses")
@Tag(name = "Refund Status", description = "Refund status management APIs")
public class RefundStatusController {

    @Autowired
    private RefundStatusService refundStatusService;

    @PatchMapping("/{uuid}/status")
    @RequiresPermission("payment:write")
    @Operation(summary = "Toggle refund status", description = "Activates or deactivates a refund status")
    public ResponseEntity<ApiResponse<Void>> toggleStatus(
            @PathVariable String uuid,
            @Valid @RequestBody StatusRequest request
    ) {
        boolean changed = refundStatusService.toggleStatus(uuid, request.isActive());
        String message = changed ? "Refund status updated successfully" : "Refund status is already " + (request.isActive() ? "active" : "inactive");
        return ApiResponse.success(null, message);
    }
}
