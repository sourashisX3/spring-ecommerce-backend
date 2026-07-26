package com.example.ecommerce_backend.modules.permission.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "Request object for creating a permission")
public class CreatePermissionRequest {

    @Schema(description = "Permission identifier in resource:action format", example = "product:read", requiredMode = Schema.RequiredMode.REQUIRED)
    @Pattern(regexp = "^[a-z_*]+:[a-z_*]+$", message = "Permission must be in format 'resource:action' (e.g. product:read)")
    private String permissionName;

    @Schema(description = "Description of the permission", example = "Allows reading products")
    private String permissionDescription;
}
