package com.example.ecommerce_backend.modules.role.controller;

import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.modules.role.dto.request.AssignPermissionRequest;
import com.example.ecommerce_backend.modules.role.dto.response.UserPermissionResponse;
import com.example.ecommerce_backend.modules.role.service.UserPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/permissions")
public class UserPermissionController {

    @Autowired
    private UserPermissionService userPermissionService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserPermissionResponse>>> getUserPermissions(
            @PathVariable Long userId
    ) {
        List<UserPermissionResponse> permissions = userPermissionService.getUserPermissions(userId);
        return ApiResponse.success(permissions, "User permissions retrieved successfully");
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserPermissionResponse>> assignPermission(
            @PathVariable Long userId,
            @RequestBody AssignPermissionRequest request
    ) {
        UserPermissionResponse response = userPermissionService.assignPermission(userId, request);
        return ApiResponse.created(response, "Permission assigned successfully");
    }

    @DeleteMapping("/{userPermissionId}")
    public ResponseEntity<ApiResponse<Void>> removePermission(
            @PathVariable Long userId,
            @PathVariable Long userPermissionId
    ) {
        userPermissionService.removePermission(userPermissionId);
        return ApiResponse.success(null, "Permission removed successfully");
    }
}
