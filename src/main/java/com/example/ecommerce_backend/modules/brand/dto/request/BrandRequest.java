package com.example.ecommerce_backend.modules.brand.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Brand request")
public class BrandRequest {

    @NotBlank(message = "Brand name is required")
    @Schema(description = "Brand name", example = "Nike", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Brand description", example = "Just Do It")
    private String description;

    @Schema(description = "Brand logo URL", example = "https://example.com/logo.png")
    private String logoUrl;

    @Schema(description = "Brand website", example = "https://nike.com")
    private String website;
}
