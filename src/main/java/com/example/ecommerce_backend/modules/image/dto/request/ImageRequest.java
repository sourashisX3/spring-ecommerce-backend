package com.example.ecommerce_backend.modules.image.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request to add an image to a product")
public class ImageRequest {

    @NotBlank(message = "Image URL is required")
    @Schema(description = "URL of the image", example = "https://example.com/images/product.jpg", requiredMode = Schema.RequiredMode.REQUIRED)
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
