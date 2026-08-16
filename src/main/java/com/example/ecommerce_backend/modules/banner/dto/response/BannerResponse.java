package com.example.ecommerce_backend.modules.banner.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Banner response")
public class BannerResponse {

    @Schema(description = "Banner UUID", example = "banner-uuid-123")
    private String uuid;
    @Schema(description = "Banner title", example = "Summer Sale")
    private String title;
    @Schema(description = "Banner subtitle", example = "Up to 40% off premium spirits")
    private String subtitle;
    @Schema(description = "Banner image URL", example = "https://example.com/banner.jpg")
    private String imageUrl;
    @Schema(description = "Banner link target type", example = "PRODUCT")
    private String linkType;
    @Schema(description = "Banner link target value", example = "product-uuid-123")
    private String linkValue;
    @Schema(description = "Display sort order", example = "1")
    private int sortOrder;
    @Schema(description = "Whether the banner is active", example = "true")
    private boolean isActive;
    @Schema(description = "Banner valid from timestamp")
    private Instant validFrom;
    @Schema(description = "Banner valid until timestamp")
    private Instant validUntil;
    @Schema(description = "Creation timestamp")
    private Instant createdAt;
    @Schema(description = "Update timestamp")
    private Instant updatedAt;

    @JsonProperty("isActive")
    public boolean isActive() {
        return isActive;
    }

    @JsonProperty("isActive")
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
}