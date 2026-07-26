package com.example.ecommerce_backend.modules.shipping.controller;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.core.dto.StatusRequest;
import com.example.ecommerce_backend.modules.shipping.service.DeliveryStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/delivery-statuses")
@Tag(name = "Delivery Status", description = "Delivery status management APIs")
public class DeliveryStatusController {

    @Autowired
    private DeliveryStatusService deliveryStatusService;

    @PatchMapping("/{uuid}/status")
    @RequiresPermission("delivery:write")
    @Operation(summary = "Toggle delivery status", description = "Activates or deactivates a delivery status")
    public ResponseEntity<ApiResponse<Void>> toggleStatus(
            @PathVariable String uuid,
            @Valid @RequestBody StatusRequest request
    ) {
        boolean changed = deliveryStatusService.toggleStatus(uuid, request.isActive());
        String message = changed ? "Delivery status updated successfully" : "Delivery status is already " + (request.isActive() ? "active" : "inactive");
        return ApiResponse.success(null, message);
    }
}
