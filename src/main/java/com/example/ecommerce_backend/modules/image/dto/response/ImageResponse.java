package com.example.ecommerce_backend.modules.image.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response containing image details")
public class ImageResponse {

    @Schema(description = "Internal ID of the image", example = "1")
    private Long id;
    @Schema(description = "Unique identifier of the image", example = "image-uuid-123")
    private String uuid;
    @Schema(description = "URL of the image", example = "https://example.com/images/product.jpg")
    private String imageUrl;
    @Schema(description = "Whether this is the primary image", example = "true")
    private boolean isPrimary;
    @Schema(description = "Sort order of the image", example = "1")
    private int sortOrder;

    @JsonProperty("isPrimary")
    public boolean isPrimary() {
        return isPrimary;
    }

    @JsonProperty("isPrimary")
    public void setPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }
}
