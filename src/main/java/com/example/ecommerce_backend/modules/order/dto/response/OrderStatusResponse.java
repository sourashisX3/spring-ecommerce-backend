package com.example.ecommerce_backend.modules.order.dto.response;

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
@Schema(description = "Order status response")
public class OrderStatusResponse {

    @Schema(description = "Status ID", example = "1")
    private Long id;
    @Schema(description = "Status UUID", example = "status-uuid-123")
    private String uuid;
    @Schema(description = "Status code", example = "PENDING")
    private String code;
    @Schema(description = "Status name", example = "Pending")
    private String name;
    @Schema(description = "Status description", example = "Order is pending")
    private String description;
    @Schema(description = "Sort order", example = "1")
    private int sortOrder;

    @JsonProperty("isActive")
    @Schema(description = "Whether status is active", example = "true")
    private boolean isActive;

    @Schema(description = "Creation timestamp")
    private Instant createdAt;
    @Schema(description = "Update timestamp")
    private Instant updatedAt;
}
