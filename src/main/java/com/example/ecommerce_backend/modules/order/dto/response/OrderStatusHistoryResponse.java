package com.example.ecommerce_backend.modules.order.dto.response;

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
@Schema(description = "Order status history response")
public class OrderStatusHistoryResponse {

    @Schema(description = "History entry ID", example = "1")
    private Long id;
    @Schema(description = "History entry UUID", example = "history-uuid-123")
    private String uuid;
    @Schema(description = "Previous status")
    private OrderStatusResponse fromStatus;
    @Schema(description = "New status")
    private OrderStatusResponse toStatus;
    @Schema(description = "Changed by", example = "admin@example.com")
    private String changedBy;
    @Schema(description = "Reason for change", example = "Item is out of stock")
    private String reason;
    @Schema(description = "Creation timestamp")
    private Instant createdAt;
}
