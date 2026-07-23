package com.example.ecommerce_backend.modules.role.dto.request;

import com.example.ecommerce_backend.modules.role.entity.Permission;
import lombok.Data;

import java.util.Set;

@Data
public class RoleRequest {

    private String roleName;
    private String roleDescription;
    private Set<Permission> rolePermissionNames;

}
