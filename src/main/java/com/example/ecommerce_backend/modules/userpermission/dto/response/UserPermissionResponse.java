package com.example.ecommerce_backend.modules.userpermission.dto.response;

import com.example.ecommerce_backend.modules.userpermission.entity.UserPermission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPermissionResponse {

    private Long id;
    private Long userId;
    private String permissionName;
    private UserPermission.Effect effect;
}
