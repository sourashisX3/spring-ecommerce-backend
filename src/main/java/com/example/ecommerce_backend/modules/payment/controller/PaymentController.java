package com.example.ecommerce_backend.modules.payment.controller;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.core.dto.Pagination;
import com.example.ecommerce_backend.modules.payment.dto.request.PaymentRequest;
import com.example.ecommerce_backend.modules.payment.dto.request.RefundRequest;
import com.example.ecommerce_backend.modules.payment.dto.response.PaymentResponse;
import com.example.ecommerce_backend.modules.payment.dto.response.RefundResponse;
import com.example.ecommerce_backend.modules.payment.service.PaymentService;
import com.example.ecommerce_backend.modules.user.entity.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/pay")
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(
            @Valid @RequestBody PaymentRequest request,
            @AuthenticationPrincipal User user) {
        PaymentResponse payment = paymentService.processPayment(request, user.getId());
        return ApiResponse.created(payment, "Payment processed successfully");
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getByUuid(@PathVariable String uuid) {
        PaymentResponse payment = paymentService.getByUuid(uuid);
        return ApiResponse.success(payment, "Payment retrieved successfully");
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getByOrderId(@PathVariable Long orderId) {
        PaymentResponse payment = paymentService.getByOrderId(orderId);
        return ApiResponse.success(payment, "Payment retrieved successfully");
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getUserPayments(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size);
            Page<PaymentResponse> payments = paymentService.getUserPayments(user.getId(), pageable);
            return ApiResponse.paginated(payments.getContent(), "Payments retrieved successfully", Pagination.of(payments));
        }
        List<PaymentResponse> payments = paymentService.getUserPayments(user.getId());
        return ApiResponse.success(payments, "Payments retrieved successfully");
    }

    @GetMapping("/{paymentId}/refunds")
    public ResponseEntity<ApiResponse<List<RefundResponse>>> getRefunds(
            @PathVariable Long paymentId) {
        List<RefundResponse> refunds = paymentService.getRefundsByPaymentId(paymentId);
        return ApiResponse.success(refunds, "Refunds retrieved successfully");
    }

    @PostMapping("/refund")
    @RequiresPermission("payment:write")
    public ResponseEntity<ApiResponse<RefundResponse>> processRefund(
            @Valid @RequestBody RefundRequest request) {
        RefundResponse refund = paymentService.processRefund(request);
        return ApiResponse.created(refund, "Refund processed successfully");
    }
}
