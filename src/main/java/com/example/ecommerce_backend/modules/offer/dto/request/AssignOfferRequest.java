package com.example.ecommerce_backend.modules.offer.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Assign offer request")
public class AssignOfferRequest {

    @NotEmpty(message = "User UUIDs are required")
    @Schema(description = "List of user UUIDs", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> userUuids;

    @Schema(description = "Usage limit per user", example = "5")
    private Integer usageLimitPerUser;
}
