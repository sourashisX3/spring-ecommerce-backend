package com.example.ecommerce_backend.modules.role.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object for role data")
public class RolesResponse {

    @Schema(description = "Role unique identifier", example = "1")
    private Long id;
    @Schema(description = "Role name", example = "ADMIN")
    private String roleName;
    @Schema(description = "Role description", example = "Administrator role with full access")
    private String roleDescription;
    @Schema(description = "Set of permission names", example = "[\"product:read\", \"product:write\"]")
    private Set<String> rolePermissions;

}
