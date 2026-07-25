package com.example.ecommerce_backend.modules.role.controller;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.core.dto.Pagination;
import com.example.ecommerce_backend.modules.role.dto.request.RoleRequest;
import com.example.ecommerce_backend.modules.role.dto.response.RolesResponse;
import com.example.ecommerce_backend.modules.role.service.RolesService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RolesController {

    @Autowired
    private RolesService rolesService;

    @GetMapping
    @RequiresPermission("role:read")
    public ResponseEntity<ApiResponse<List<RolesResponse>>> getAllRoles(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<RolesResponse> roles = rolesService.getAllRoles(search, pageable);
        return ApiResponse.paginated(roles.getContent(), "Roles retrieved successfully", Pagination.of(roles));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RolesResponse>> createRole(
            @Valid @RequestBody RoleRequest request
    ) {
        RolesResponse role = rolesService.createRole(request);

        return ApiResponse.created(role, "Role created successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable Long id) {
        rolesService.deleteRole(id);
        return ApiResponse.success(null, "Role deleted successfully");
    }

}
