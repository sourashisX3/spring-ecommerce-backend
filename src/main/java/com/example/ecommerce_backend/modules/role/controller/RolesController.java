package com.example.ecommerce_backend.modules.role.controller;

import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.modules.role.dto.request.RoleRequest;
import com.example.ecommerce_backend.modules.role.dto.response.RolesResponse;
import com.example.ecommerce_backend.modules.role.entity.Role;
import com.example.ecommerce_backend.modules.role.service.RolesService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RolesController {

    @Autowired
    private RolesService rolesService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RolesResponse>>> getAllRoles() {
        List<RolesResponse> roles = rolesService.getAllRoles();
        return ApiResponse.success(roles, "Roles retrieved successfully");
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
