package com.example.ecommerce_backend.modules.permission.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response object for permission data")
public class PermissionResponse {

    @Schema(description = "Permission unique identifier", example = "1")
    private Long id;
    @Schema(description = "Permission name", example = "product:read")
    private String permissionName;
    @Schema(description = "Permission description", example = "Allows reading products")
    private String permissionDescription;
}
