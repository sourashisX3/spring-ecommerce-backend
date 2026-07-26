package com.example.ecommerce_backend.modules.permission.controller;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.core.dto.Pagination;
import com.example.ecommerce_backend.modules.permission.dto.request.CreatePermissionRequest;
import com.example.ecommerce_backend.modules.permission.dto.response.PermissionResponse;
import com.example.ecommerce_backend.modules.permission.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions")
@Tag(name = "Permission", description = "Permission API")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @GetMapping
    @Operation(summary = "Get all permissions", description = "Retrieves a paginated list of all permissions")
    @RequiresPermission("permission:read")
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> getAllPermissions(
            @ParameterObject @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<PermissionResponse> permissions = permissionService.getAllPermissions(pageable);
        return ApiResponse.paginated(permissions.getContent(), "Permissions retrieved successfully", Pagination.of(permissions));
    }

    @PostMapping
    @Operation(summary = "Create a permission", description = "Creates a new permission")
    public ResponseEntity<ApiResponse<PermissionResponse>> createPermission(
            @Valid @RequestBody CreatePermissionRequest request
    ) {
        PermissionResponse response = permissionService.createPermission(request);
        return ApiResponse.created(response, "Permission created successfully");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a permission", description = "Deletes a permission by ID")
    public ResponseEntity<ApiResponse<Void>> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return ApiResponse.success(null, "Permission deleted successfully");
    }
}
