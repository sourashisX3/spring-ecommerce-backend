package com.example.ecommerce_backend.modules.role.mapper;

import com.example.ecommerce_backend.modules.permission.entity.Permission;
import com.example.ecommerce_backend.modules.role.dto.response.RolesResponse;
import com.example.ecommerce_backend.modules.role.entity.Role;

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

}
