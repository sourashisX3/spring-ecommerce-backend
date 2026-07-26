package com.example.ecommerce_backend.modules.returns.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object for return request data")
public class ReturnResponse {
    @Schema(description = "Return request UUID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String uuid;
    @Schema(description = "User UUID", example = "u1s2e3r4-e5f6-7890-abcd-ef1234567890")
    private String userUuid;
    @Schema(description = "Order UUID", example = "ord-12345")
    private String orderUuid;
    @Schema(description = "Return type")
    private ReturnTypeResponse returnType;
    @Schema(description = "Return request status", example = "PENDING")
    private String status;
    @Schema(description = "Reason for the return", example = "Item does not match description")
    private String reason;
    @Schema(description = "Resolution notes", example = "Refund processed")
    private String resolutionNotes;
    @Schema(description = "Refund amount", example = "99.99")
    private BigDecimal refundAmount;
    @Schema(description = "List of return items")
    private List<ReturnItemResponse> items;
    @Schema(description = "Creation timestamp")
    private Instant createdAt;
    @Schema(description = "Last update timestamp")
    private Instant updatedAt;
}
