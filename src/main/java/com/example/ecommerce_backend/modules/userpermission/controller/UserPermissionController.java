package com.example.ecommerce_backend.modules.userpermission.controller;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.core.dto.Pagination;
import com.example.ecommerce_backend.modules.userpermission.dto.request.AssignPermissionRequest;
import com.example.ecommerce_backend.modules.userpermission.dto.response.UserPermissionResponse;
import com.example.ecommerce_backend.modules.userpermission.service.UserPermissionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/permissions")
public class UserPermissionController {

    @Autowired
    private UserPermissionService userPermissionService;

    @GetMapping
    @RequiresPermission("user_permission:read")
    public ResponseEntity<ApiResponse<List<UserPermissionResponse>>> getUserPermissions(
            @PathVariable Long userId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<UserPermissionResponse> permissions = userPermissionService.getUserPermissions(userId, pageable);
        return ApiResponse.paginated(permissions.getContent(), "User permissions retrieved successfully", Pagination.of(permissions));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserPermissionResponse>> assignPermission(
            @PathVariable Long userId,
            @Valid @RequestBody AssignPermissionRequest request
    ) {
        UserPermissionResponse response = userPermissionService.assignPermission(userId, request);
        return ApiResponse.created(response, "Permission assigned successfully");
    }

    @DeleteMapping("/{userPermissionId}")
    public ResponseEntity<ApiResponse<Void>> removePermission(
            @PathVariable Long userPermissionId
    ) {
        userPermissionService.removePermission(userPermissionId);
        return ApiResponse.success(null, "Permission removed successfully");
    }
}
