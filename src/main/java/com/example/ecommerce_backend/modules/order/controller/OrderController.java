package com.example.ecommerce_backend.modules.order.controller;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.core.dto.Pagination;
import com.example.ecommerce_backend.modules.order.dto.request.OrderRequest;
import com.example.ecommerce_backend.modules.order.dto.request.UpdateOrderStatusRequest;
import com.example.ecommerce_backend.modules.order.dto.response.OrderResponse;
import com.example.ecommerce_backend.modules.order.service.OrderService;
import com.example.ecommerce_backend.modules.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/orders")
@Tag(name = "Order", description = "Order API")
public class OrderController {

    private static final String ADMIN = "ADMIN";
    private static final String SUPER_ADMIN = "SUPER_ADMIN";

    @Autowired
    private OrderService orderService;

    @PostMapping("/checkout")
    @Operation(summary = "Create order", description = "Create a new order from cart")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody OrderRequest request,
            @AuthenticationPrincipal User user
    ) {
        OrderResponse order = orderService.createOrder(user.getId(), request);
        return ApiResponse.created(order, "Order created successfully");
    }

    @GetMapping
    @Operation(summary = "Get orders", description = "Retrieve orders with optional search, status/date filters, sorting and pagination. Admins see all orders, regular users only their own.")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrders(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDir,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        if (sortBy != null) {
            Sort.Direction direction = "desc".equalsIgnoreCase(sortDir)
                    ? Sort.Direction.DESC : Sort.Direction.ASC;
            sort = Sort.by(direction, sortBy);
        }

        boolean adminView = isAdmin(user);

        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<OrderResponse> orders = adminView
                    ? orderService.getAllOrders(search, status, from, to, pageable)
                    : orderService.getUserOrders(user.getId(), pageable);
            return ApiResponse.paginated(orders.getContent(), "Orders retrieved successfully", Pagination.of(orders));
        }
        List<OrderResponse> orders = adminView
                ? orderService.getAllOrders(search, status, from, to, sort)
                : orderService.getUserOrders(user.getId());
        return ApiResponse.success(orders, "Orders retrieved successfully");
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Get order by UUID", description = "Retrieve an order by its UUID")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderByUuid(
            @PathVariable String uuid,
            @AuthenticationPrincipal User user
    ) {
        OrderResponse order = orderService.getOrderByUuid(uuid, user.getId(), isAdmin(user));
        return ApiResponse.success(order, "Order retrieved successfully");
    }

    @PatchMapping("/{uuid}/cancel")
    @Operation(summary = "Cancel order", description = "Cancel an order by UUID")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @PathVariable String uuid,
            @AuthenticationPrincipal User user
    ) {
        OrderResponse order = orderService.cancelOrder(uuid, user.getId(), isAdmin(user));
        return ApiResponse.success(order, "Order cancelled successfully");
    }

    @PutMapping("/{uuid}/status")
    @RequiresPermission("order:update_status")
    @Operation(summary = "Update order status", description = "Update the status of an order")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable String uuid,
            @Valid @RequestBody UpdateOrderStatusRequest request
    ) {
        OrderResponse order = orderService.updateOrderStatus(uuid, request.getStatus(), request.getReason());
        return ApiResponse.success(order, "Order status updated successfully");
    }

    private boolean isAdmin(User user) {
        return user != null && user.getRole() != null
                && (ADMIN.equals(user.getRole().getRoleName())
                    || SUPER_ADMIN.equals(user.getRole().getRoleName()));
    }
}
