package com.example.ecommerce_backend.modules.brand.dto.response;

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
@Schema(description = "Brand response")
public class BrandResponse {

    @Schema(description = "Brand ID", example = "1")
    private Long id;

    @Schema(description = "Brand UUID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String uuid;

    @Schema(description = "Brand name", example = "Nike")
    private String name;

    @Schema(description = "Brand slug", example = "nike")
    private String slug;

    @Schema(description = "Brand description", example = "Just Do It")
    private String description;

    @Schema(description = "Brand logo URL", example = "https://example.com/logo.png")
    private String logoUrl;

    @Schema(description = "Brand website", example = "https://nike.com")
    private String website;

    private boolean isActive;

    @Schema(description = "Number of products", example = "10")
    private long productCount;

    @Schema(description = "Creation timestamp")
    private Instant createdAt;

    @Schema(description = "Last update timestamp")
    private Instant updatedAt;

    @JsonProperty("isActive")
    @Schema(description = "Whether the brand is active", example = "true")
    public boolean isActive() {
        return isActive;
    }

    @JsonProperty("isActive")
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
}
