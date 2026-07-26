package com.example.ecommerce_backend.modules.role.controller;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.core.dto.Pagination;
import com.example.ecommerce_backend.modules.role.dto.request.RoleRequest;
import com.example.ecommerce_backend.modules.role.dto.response.RolesResponse;
import com.example.ecommerce_backend.modules.role.service.RolesService;
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
@RequestMapping("/roles")
@Tag(name = "Role", description = "Role API")
public class RolesController {

    @Autowired
    private RolesService rolesService;

    @GetMapping
    @Operation(summary = "Get all roles", description = "Retrieves a paginated list of all roles")
    @RequiresPermission("role:read")
    public ResponseEntity<ApiResponse<List<RolesResponse>>> getAllRoles(
            @RequestParam(required = false) String search,
            @ParameterObject @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<RolesResponse> roles = rolesService.getAllRoles(search, pageable);
        return ApiResponse.paginated(roles.getContent(), "Roles retrieved successfully", Pagination.of(roles));
    }

    @PostMapping
    @Operation(summary = "Create a role", description = "Creates a new role")
    public ResponseEntity<ApiResponse<RolesResponse>> createRole(
            @Valid @RequestBody RoleRequest request
    ) {
        RolesResponse role = rolesService.createRole(request);

        return ApiResponse.created(role, "Role created successfully");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a role", description = "Deletes a role by ID")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable Long id) {
        rolesService.deleteRole(id);
        return ApiResponse.success(null, "Role deleted successfully");
    }

}
