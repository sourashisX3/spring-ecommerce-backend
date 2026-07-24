package com.example.ecommerce_backend.modules.role_user.dto.request;

import com.example.ecommerce_backend.modules.role_user.entity.UserPermission;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignPermissionRequest {

    @NotNull(message = "Permission ID is required")
    private Long permissionId;

    @NotNull(message = "Effect is required")
    private UserPermission.Effect effect;
}
