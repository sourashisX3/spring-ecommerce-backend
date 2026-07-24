package com.example.ecommerce_backend.modules.role_user.mapper;

import com.example.ecommerce_backend.modules.role_user.dto.response.PermissionResponse;
import com.example.ecommerce_backend.modules.role_user.dto.response.RolesResponse;
import com.example.ecommerce_backend.modules.role_user.entity.Permission;
import com.example.ecommerce_backend.modules.role_user.entity.Role;

import java.util.Set;
import java.util.stream.Collectors;

public class RolesMapper {

    private RolesMapper() {
    }

    public static RolesResponse toRoleResponse(Role role) {
        Set<String> permissionNames = role.getPermissions().stream()
                .map(Permission::getPermissionName)
                .collect(Collectors.toSet());

        return RolesResponse.builder()
                .id(role.getId())
                .roleName(role.getRoleName())
                .roleDescription(role.getRoleDescription())
                .rolePermissions(permissionNames)
                .build();
    }

    public static PermissionResponse toPermissionResponse(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .permissionName(permission.getPermissionName())
                .permissionDescription(permission.getPermissionDescription())
                .build();
    }

}
