package com.example.ecommerce_backend.modules.userpermission.mapper;

import com.example.ecommerce_backend.modules.userpermission.dto.response.UserPermissionResponse;
import com.example.ecommerce_backend.modules.userpermission.entity.UserPermission;

public class UserPermissionMapper {

    private UserPermissionMapper() {
    }

    public static UserPermissionResponse toUserPermissionResponse(UserPermission userPermission) {
        return UserPermissionResponse.builder()
                .id(userPermission.getId())
                .userId(userPermission.getUser().getId())
                .permissionName(userPermission.getPermission().getPermissionName())
                .effect(userPermission.getEffect())
                .build();
    }
}
