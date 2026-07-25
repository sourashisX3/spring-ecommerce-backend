package com.example.ecommerce_backend.modules.product.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ImageRequest {

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    private boolean isPrimary;

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
