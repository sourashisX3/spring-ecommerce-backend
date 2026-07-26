package com.example.ecommerce_backend.modules.returns.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Request object for creating a return request")
public class ReturnRequestDto {
    @Schema(description = "UUID of the order being returned", example = "ord-12345", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String orderUuid;

    @Schema(description = "Return type code", example = "REFUND")
    private String returnTypeCode;

    @Schema(description = "Reason for the return", example = "Item does not match description", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String reason;

    @Schema(description = "List of items being returned", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private List<ReturnItemDto> items;

    @Data
    @Schema(description = "Item in a return request")
    public static class ReturnItemDto {
        @Schema(description = "Order item ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        private Long orderItemId;

        @Schema(description = "Quantity being returned", example = "1")
        private int quantity;

        @Schema(description = "Condition code of the returned item", example = "DAMAGED")
        private String conditionCode;

        @Schema(description = "Note about the item condition", example = "Item arrived damaged")
        private String conditionNote;
    }
}
