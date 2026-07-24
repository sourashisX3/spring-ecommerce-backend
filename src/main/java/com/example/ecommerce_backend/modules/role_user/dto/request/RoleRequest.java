package com.example.ecommerce_backend.modules.role_user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

@Data
public class RoleRequest {

    @NotBlank(message = "Role name is required")
    private String roleName;

    private String roleDescription;

    @NotEmpty(message = "At least one permission is required")
    private Set<Long> rolePermissionIds;

}
