package com.example.ecommerce_backend.modules.role.dto.request;

import lombok.Data;

import java.util.Set;

@Data
public class RoleRequest {

    private String roleName;
    private String roleDescription;
    private Set<String> rolePermissionNames;

}
