package com.example.ecommerce_backend.modules.role.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

@Data
@Schema(description = "Request object for creating or updating a role")
public class RoleRequest {

    @Schema(description = "Role name", example = "ADMIN", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Role name is required")
    private String roleName;

    @Schema(description = "Role description", example = "Administrator role with full access")
    private String roleDescription;

    @Schema(description = "Set of permission IDs", example = "[1, 2, 3]", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "At least one permission is required")
    private Set<Long> rolePermissionIds;

}
