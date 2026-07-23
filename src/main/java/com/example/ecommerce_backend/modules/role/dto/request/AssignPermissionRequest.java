package com.example.ecommerce_backend.modules.role.dto.request;

import com.example.ecommerce_backend.modules.role.entity.UserPermission;
import lombok.Data;

@Data
public class AssignPermissionRequest {

    private Long permissionId;

    private UserPermission.Effect effect;
}
