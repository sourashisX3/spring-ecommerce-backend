package com.example.ecommerce_backend.modules.userpermission.dto.response;

import com.example.ecommerce_backend.modules.userpermission.entity.UserPermission;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User permission response")
public class UserPermissionResponse {

    @Schema(description = "User permission ID", example = "1")
    private Long id;
    @Schema(description = "User ID", example = "1")
    private Long userId;
    @Schema(description = "Permission name", example = "user:read")
    private String permissionName;
    @Schema(description = "Permission effect")
    private UserPermission.Effect effect;
}
