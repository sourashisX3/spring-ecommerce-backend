package com.example.ecommerce_backend.modules.tag.dto.response;

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
@Schema(description = "Tag response")
public class TagResponse {

    @Schema(description = "Tag ID", example = "1")
    private Long id;
    @Schema(description = "Tag UUID", example = "550e8400-e29b-41d4-a716-446655440000")
    private String uuid;
    @Schema(description = "Tag name", example = "Electronics")
    private String name;
    @Schema(description = "Tag slug", example = "electronics")
    private String slug;
    @Schema(description = "Whether the tag is active", example = "true")
    private boolean isActive;
    @Schema(description = "Creation timestamp")
    private Instant createdAt;
    @Schema(description = "Last update timestamp")
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
