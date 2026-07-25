package com.example.ecommerce_backend.modules.returns.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ReturnRequestDto {
    @NotBlank
    private String orderUuid;

    private String returnTypeCode;

    @NotBlank
    private String reason;

    @NotNull
    private List<ReturnItemDto> items;

    @Data
    public static class ReturnItemDto {
        @NotNull
        private Long orderItemId;

        private int quantity;

        private String conditionCode;

        private String conditionNote;
    }
}
