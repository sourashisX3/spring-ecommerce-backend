package com.example.ecommerce_backend.modules.banner.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.Instant;

@Data
@Schema(description = "Banner request")
public class BannerRequest {

    @NotBlank(message = "Title is required")
    @Schema(description = "Banner title", example = "Summer Sale", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "Banner subtitle", example = "Up to 40% off premium spirits")
    private String subtitle;

    @NotBlank(message = "Image URL is required")
    @Schema(description = "Banner image URL", example = "https://example.com/banner.jpg", requiredMode = Schema.RequiredMode.REQUIRED)
    private String imageUrl;

    @NotBlank(message = "Link type is required")
    @Schema(description = "Banner link target type", example = "PRODUCT", requiredMode = Schema.RequiredMode.REQUIRED)
    private String linkType;

    @Schema(description = "Banner link target value", example = "product-uuid-123")
    private String linkValue;

    @Schema(description = "Display sort order", example = "1")
    private Integer sortOrder;

    @Schema(description = "Whether the banner is active", example = "true")
    private Boolean isActive;

    @Schema(description = "Banner valid from timestamp")
    private Instant validFrom;

    @Schema(description = "Banner valid until timestamp")
    private Instant validUntil;
}