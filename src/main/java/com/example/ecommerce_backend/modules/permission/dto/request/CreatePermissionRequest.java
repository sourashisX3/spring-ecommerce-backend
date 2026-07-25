package com.example.ecommerce_backend.modules.permission.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreatePermissionRequest {

    @Pattern(regexp = "^[a-z_*]+:[a-z_*]+$", message = "Permission must be in format 'resource:action' (e.g. product:read)")
    private String permissionName;

    private String permissionDescription;
}
