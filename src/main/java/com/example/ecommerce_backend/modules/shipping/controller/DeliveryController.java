package com.example.ecommerce_backend.modules.shipping.controller;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.modules.shipping.dto.request.UpdateDeliveryRequest;
import com.example.ecommerce_backend.modules.shipping.dto.response.DeliveryResponse;
import com.example.ecommerce_backend.modules.shipping.service.DeliveryService;
import com.example.ecommerce_backend.modules.user.entity.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<List<DeliveryResponse>>> getByOrderId(
            @PathVariable Long orderId,
            @AuthenticationPrincipal User user
    ) {
        List<DeliveryResponse> deliveries = deliveryService.getByOrderId(orderId);
        return ApiResponse.success(deliveries, "Deliveries retrieved successfully");
    }

    @PutMapping("/{uuid}")
    @RequiresPermission("delivery:write")
    public ResponseEntity<ApiResponse<DeliveryResponse>> updateDelivery(
            @PathVariable String uuid,
            @Valid @RequestBody UpdateDeliveryRequest request
    ) {
        DeliveryResponse delivery = deliveryService.updateDelivery(uuid, request);
        return ApiResponse.success(delivery, "Delivery updated successfully");
    }
}
