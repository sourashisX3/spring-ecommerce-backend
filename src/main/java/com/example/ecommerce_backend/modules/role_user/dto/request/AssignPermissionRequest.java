package com.example.ecommerce_backend.modules.role_user.dto.request;

import com.example.ecommerce_backend.modules.role_user.entity.UserPermission;
import lombok.Data;

@Data
public class AssignPermissionRequest {

    private Long permissionId;

    private UserPermission.Effect effect;
}
