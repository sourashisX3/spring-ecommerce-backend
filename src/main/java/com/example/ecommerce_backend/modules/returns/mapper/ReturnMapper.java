package com.example.ecommerce_backend.modules.returns.mapper;

import com.example.ecommerce_backend.modules.returns.dto.response.ReturnConditionResponse;
import com.example.ecommerce_backend.modules.returns.dto.response.ReturnItemResponse;
import com.example.ecommerce_backend.modules.returns.dto.response.ReturnResponse;
import com.example.ecommerce_backend.modules.returns.dto.response.ReturnTypeResponse;
import com.example.ecommerce_backend.modules.returns.entity.ReturnCondition;
import com.example.ecommerce_backend.modules.returns.entity.ReturnItem;
import com.example.ecommerce_backend.modules.returns.entity.ReturnRequest;
import com.example.ecommerce_backend.modules.returns.entity.ReturnType;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ReturnMapper {

    public static ReturnTypeResponse toReturnTypeResponse(ReturnType entity) {
        if (entity == null) return null;
        return ReturnTypeResponse.builder()
                .uuid(entity.getUuid())
                .code(entity.getCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .isActive(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static ReturnConditionResponse toReturnConditionResponse(ReturnCondition entity) {
        if (entity == null) return null;
        return ReturnConditionResponse.builder()
                .uuid(entity.getUuid())
                .code(entity.getCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .isActive(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static ReturnItemResponse toReturnItemResponse(ReturnItem entity) {
        if (entity == null) return null;
        return ReturnItemResponse.builder()
                .uuid(entity.getUuid())
                .orderItemId(entity.getOrderItemId())
                .productId(entity.getProductId())
                .productName(entity.getProductName())
                .sku(entity.getSku())
                .quantity(entity.getQuantity())
                .unitPrice(entity.getUnitPrice())
                .condition(toReturnConditionResponse(entity.getCondition()))
                .conditionNote(entity.getConditionNote())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public static ReturnResponse toReturnResponse(ReturnRequest entity) {
        if (entity == null) return null;
        return ReturnResponse.builder()
                .id(entity.getId())
                .uuid(entity.getUuid())
                .userUuid(entity.getUser() != null ? entity.getUser().getUuid() : null)
                .orderUuid(entity.getOrder() != null ? entity.getOrder().getUuid() : null)
                .returnType(toReturnTypeResponse(entity.getReturnType()))
                .status(entity.getStatus() != null ? entity.getStatus().getCode() : null)
                .reason(entity.getReason())
                .resolutionNotes(entity.getResolutionNotes())
                .refundAmount(entity.getRefundAmount())
                .items(entity.getItems() != null
                        ? entity.getItems().stream()
                                .map(ReturnMapper::toReturnItemResponse)
                                .collect(Collectors.toList())
                        : Collections.emptyList())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
