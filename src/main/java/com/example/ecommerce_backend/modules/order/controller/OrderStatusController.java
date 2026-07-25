package com.example.ecommerce_backend.modules.order.controller;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.modules.order.dto.request.OrderStatusRequest;
import com.example.ecommerce_backend.modules.order.dto.response.OrderStatusResponse;
import com.example.ecommerce_backend.modules.order.service.OrderStatusService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-statuses")
public class OrderStatusController {

    @Autowired
    private OrderStatusService orderStatusService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderStatusResponse>>> getAll() {
        List<OrderStatusResponse> statuses = orderStatusService.getAll();
        return ApiResponse.success(statuses, "Order statuses retrieved successfully");
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponse<OrderStatusResponse>> getByUuid(@PathVariable String uuid) {
        OrderStatusResponse status = orderStatusService.getByUuid(uuid);
        return ApiResponse.success(status, "Order status retrieved successfully");
    }

    @PostMapping
    @RequiresPermission("order:write")
    public ResponseEntity<ApiResponse<OrderStatusResponse>> create(
            @Valid @RequestBody OrderStatusRequest request) {
        OrderStatusResponse status = orderStatusService.create(request);
        return ApiResponse.created(status, "Order status created successfully");
    }

    @PutMapping("/{uuid}")
    @RequiresPermission("order:write")
    public ResponseEntity<ApiResponse<OrderStatusResponse>> update(
            @PathVariable String uuid,
            @Valid @RequestBody OrderStatusRequest request) {
        OrderStatusResponse status = orderStatusService.update(uuid, request);
        return ApiResponse.success(status, "Order status updated successfully");
    }

    @PatchMapping("/{uuid}/status")
    @RequiresPermission("order:write")
    public ResponseEntity<ApiResponse<OrderStatusResponse>> toggleStatus(
            @PathVariable String uuid,
            @RequestParam boolean active) {
        OrderStatusResponse status = orderStatusService.toggleStatus(uuid, active);
        return ApiResponse.success(status, "Order status updated successfully");
    }

    @DeleteMapping("/{uuid}")
    @RequiresPermission("order:write")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String uuid) {
        orderStatusService.delete(uuid);
        return ApiResponse.success(null, "Order status deleted successfully");
    }
}
