package com.example.ecommerce_backend.modules.payment.mapper;

import com.example.ecommerce_backend.modules.payment.dto.response.PaymentGatewayResponse;
import com.example.ecommerce_backend.modules.payment.dto.response.PaymentResponse;
import com.example.ecommerce_backend.modules.payment.dto.response.RefundResponse;
import com.example.ecommerce_backend.modules.payment.entity.Payment;
import com.example.ecommerce_backend.modules.payment.entity.PaymentGateway;
import com.example.ecommerce_backend.modules.payment.entity.Refund;

public class PaymentMapper {
    private PaymentMapper() {}

    public static PaymentGatewayResponse toGatewayResponse(PaymentGateway gateway) {
        if (gateway == null) return null;
        return PaymentGatewayResponse.builder()
                .id(gateway.getId())
                .uuid(gateway.getUuid())
                .code(gateway.getCode())
                .name(gateway.getName())
                .description(gateway.getDescription())
                .configTemplate(gateway.getConfigTemplate())
                .isActive(gateway.isActive())
                .createdAt(gateway.getCreatedAt())
                .updatedAt(gateway.getUpdatedAt())
                .build();
    }

    public static PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .uuid(payment.getUuid())
                .orderId(payment.getOrderId())
                .userId(payment.getUser().getId())
                .gateway(toGatewayResponse(payment.getGateway()))
                .amount(payment.getAmount())
                .currency(payment.getCurrency() != null ? payment.getCurrency().getCode() : null)
                .status(payment.getStatus() != null ? payment.getStatus().getCode() : null)
                .method(payment.getMethod())
                .gatewayTransactionId(payment.getGatewayTransactionId())
                .paidAt(payment.getPaidAt())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }

    public static RefundResponse toRefundResponse(Refund refund) {
        return RefundResponse.builder()
                .id(refund.getId())
                .uuid(refund.getUuid())
                .paymentId(refund.getPayment().getId())
                .returnRequestId(refund.getReturnRequestId())
                .amount(refund.getAmount())
                .reason(refund.getReason())
                .status(refund.getStatus() != null ? refund.getStatus().getCode() : null)
                .currency(refund.getPayment().getCurrency() != null
                        ? refund.getPayment().getCurrency().getCode() : null)
                .gatewayRefundId(refund.getGatewayRefundId())
                .refundedAt(refund.getRefundedAt())
                .createdAt(refund.getCreatedAt())
                .updatedAt(refund.getUpdatedAt())
                .build();
    }
}
