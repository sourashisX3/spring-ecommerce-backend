package com.example.ecommerce_backend.modules.tag.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Tag request")
public class TagRequest {

    @NotBlank(message = "Tag name is required")
    @Schema(description = "Tag name", example = "Electronics", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
}
