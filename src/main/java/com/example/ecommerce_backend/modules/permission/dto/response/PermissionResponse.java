package com.example.ecommerce_backend.modules.permission.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PermissionResponse {

    private Long id;
    private String permissionName;
    private String permissionDescription;
}
