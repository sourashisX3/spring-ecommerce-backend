package com.example.ecommerce_backend.modules.permission.mapper;

import com.example.ecommerce_backend.modules.permission.dto.response.PermissionResponse;
import com.example.ecommerce_backend.modules.permission.entity.Permission;

public class PermissionMapper {

    private PermissionMapper() {
    }

    public static PermissionResponse toPermissionResponse(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .permissionName(permission.getPermissionName())
                .permissionDescription(permission.getPermissionDescription())
                .build();
    }
}
