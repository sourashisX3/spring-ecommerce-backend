package com.example.ecommerce_backend.modules.order.controller;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.modules.order.dto.request.OrderStatusRequest;
import com.example.ecommerce_backend.modules.order.dto.response.OrderStatusResponse;
import com.example.ecommerce_backend.modules.order.service.OrderStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order-statuses")
@Tag(name = "Order Status", description = "Order Status API")
public class OrderStatusController {

    @Autowired
    private OrderStatusService orderStatusService;

    @GetMapping
    @Operation(summary = "Get all order statuses", description = "Retrieve all order statuses")
    public ResponseEntity<ApiResponse<List<OrderStatusResponse>>> getAll() {
        List<OrderStatusResponse> statuses = orderStatusService.getAll();
        return ApiResponse.success(statuses, "Order statuses retrieved successfully");
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Get order status by UUID", description = "Retrieve an order status by UUID")
    public ResponseEntity<ApiResponse<OrderStatusResponse>> getByUuid(@PathVariable String uuid) {
        OrderStatusResponse status = orderStatusService.getByUuid(uuid);
        return ApiResponse.success(status, "Order status retrieved successfully");
    }

    @PostMapping
    @RequiresPermission("order:write")
    @Operation(summary = "Create order status", description = "Create a new order status")
    public ResponseEntity<ApiResponse<OrderStatusResponse>> create(
            @Valid @RequestBody OrderStatusRequest request) {
        OrderStatusResponse status = orderStatusService.create(request);
        return ApiResponse.created(status, "Order status created successfully");
    }

    @PutMapping("/{uuid}")
    @RequiresPermission("order:write")
    @Operation(summary = "Update order status", description = "Update an existing order status")
    public ResponseEntity<ApiResponse<OrderStatusResponse>> update(
            @PathVariable String uuid,
            @Valid @RequestBody OrderStatusRequest request) {
        OrderStatusResponse status = orderStatusService.update(uuid, request);
        return ApiResponse.success(status, "Order status updated successfully");
    }

    @PatchMapping("/{uuid}/status")
    @RequiresPermission("order:write")
    @Operation(summary = "Toggle order status", description = "Activate or deactivate an order status")
    public ResponseEntity<ApiResponse<OrderStatusResponse>> toggleStatus(
            @PathVariable String uuid,
            @RequestParam boolean active) {
        OrderStatusResponse status = orderStatusService.toggleStatus(uuid, active);
        return ApiResponse.success(status, "Order status updated successfully");
    }

    @DeleteMapping("/{uuid}")
    @RequiresPermission("order:write")
    @Operation(summary = "Delete order status", description = "Delete an order status by UUID")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String uuid) {
        orderStatusService.delete(uuid);
        return ApiResponse.success(null, "Order status deleted successfully");
    }
}
