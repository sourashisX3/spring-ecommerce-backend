package com.example.ecommerce_backend.modules.returns.dto.response;

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
public class ReturnResponse {
    private String uuid;
    private String userUuid;
    private String orderUuid;
    private ReturnTypeResponse returnType;
    private String status;
    private String reason;
    private String resolutionNotes;
    private BigDecimal refundAmount;
    private List<ReturnItemResponse> items;
    private Instant createdAt;
    private Instant updatedAt;
}
