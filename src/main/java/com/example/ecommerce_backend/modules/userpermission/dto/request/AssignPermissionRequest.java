package com.example.ecommerce_backend.modules.userpermission.dto.request;

import com.example.ecommerce_backend.modules.userpermission.entity.UserPermission;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Assign permission request")
public class AssignPermissionRequest {

    @NotNull(message = "Permission ID is required")
    @Schema(description = "Permission ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long permissionId;

    @NotNull(message = "Effect is required")
    @Schema(description = "Permission effect", example = "ALLOW", requiredMode = Schema.RequiredMode.REQUIRED)
    private UserPermission.Effect effect;
}
