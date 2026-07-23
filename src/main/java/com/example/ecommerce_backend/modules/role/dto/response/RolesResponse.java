package com.example.ecommerce_backend.modules.role.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolesResponse {

    private Long id;
    private String roleName;
    private String roleDescription;
    private Set<String> rolePermissions;

}
