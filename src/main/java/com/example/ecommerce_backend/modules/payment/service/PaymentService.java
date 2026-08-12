package com.example.ecommerce_backend.modules.payment.service;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.modules.order.entity.Order;
import com.example.ecommerce_backend.modules.order.entity.OrderStatus;
import com.example.ecommerce_backend.modules.order.exception.OrderNotFoundException;
import com.example.ecommerce_backend.modules.order.exception.OrderStatusNotFoundException;
import com.example.ecommerce_backend.modules.order.repository.OrderRepository;
import com.example.ecommerce_backend.modules.order.repository.OrderStatusRepository;
import com.example.ecommerce_backend.modules.payment.dto.request.PaymentRequest;
import com.example.ecommerce_backend.modules.payment.dto.request.RefundRequest;
import com.example.ecommerce_backend.modules.payment.dto.response.PaymentResponse;
import com.example.ecommerce_backend.modules.payment.dto.response.RefundResponse;
import com.example.ecommerce_backend.modules.payment.entity.Payment;
import com.example.ecommerce_backend.modules.payment.entity.PaymentGateway;
import com.example.ecommerce_backend.modules.payment.entity.PaymentStatus;
import com.example.ecommerce_backend.modules.payment.entity.Refund;
import com.example.ecommerce_backend.modules.payment.entity.RefundStatus;
import com.example.ecommerce_backend.modules.payment.exception.PaymentFailedException;
import com.example.ecommerce_backend.modules.payment.exception.PaymentGatewayNotFoundException;
import com.example.ecommerce_backend.modules.payment.exception.PaymentNotFoundException;
import com.example.ecommerce_backend.modules.payment.exception.PaymentStatusNotFoundException;
import com.example.ecommerce_backend.modules.payment.exception.InvalidRefundException;
import com.example.ecommerce_backend.modules.payment.exception.RefundStatusNotFoundException;
import com.example.ecommerce_backend.modules.payment.mapper.PaymentMapper;
import com.example.ecommerce_backend.modules.currency.entity.Currency;
import com.example.ecommerce_backend.modules.currency.exception.CurrencyNotFoundException;
import com.example.ecommerce_backend.modules.currency.repository.CurrencyRepository;
import com.example.ecommerce_backend.modules.payment.repository.PaymentGatewayRepository;
import com.example.ecommerce_backend.modules.payment.repository.PaymentRepository;
import com.example.ecommerce_backend.modules.payment.repository.PaymentStatusRepository;
import com.example.ecommerce_backend.modules.payment.repository.RefundRepository;
import com.example.ecommerce_backend.modules.payment.repository.RefundStatusRepository;
import com.example.ecommerce_backend.core.event.PaymentFailedEvent;
import com.example.ecommerce_backend.core.event.PaymentProcessedEvent;
import com.example.ecommerce_backend.modules.user.entity.User;
import com.example.ecommerce_backend.modules.user.exception.UserNotFoundException;
import com.example.ecommerce_backend.modules.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentGatewayRepository paymentGatewayRepository;

    @Autowired
    private RefundRepository refundRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @Autowired
    private PaymentStatusRepository paymentStatusRepository;

    @Autowired
    private RefundStatusRepository refundStatusRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private MockPaymentGateway mockPaymentGateway;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Transactional
    public PaymentResponse processPayment(PaymentRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("id: " + userId));

        PaymentGateway gateway = paymentGatewayRepository.findByCode(request.getGatewayCode())
                .orElseThrow(() -> new PaymentGatewayNotFoundException(request.getGatewayCode()));

        if (!gateway.isActive()) {
            throw new PaymentFailedException("Selected payment gateway is not active");
        }

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException("id: " + request.getOrderId()));

        Currency currency;
        if (request.getCurrency() != null && !request.getCurrency().isBlank()) {
            currency = currencyRepository.findByCode(request.getCurrency())
                    .orElseThrow(() -> new CurrencyNotFoundException(request.getCurrency()));
        } else {
            currency = currencyRepository.findByIsDefaultTrueAndIsActiveTrue()
                    .orElseGet(() -> currencyRepository.findFirstByIsActiveTrueOrderBySortOrderAscIdAsc()
                            .orElseThrow(() -> new CurrencyNotFoundException("No active default currency")));
        }

        if (!currency.isActive()) {
            throw new PaymentFailedException("Selected currency is not active");
        }

        PaymentStatus defaultStatus = paymentStatusRepository.findByCode("PENDING")
                .orElseThrow(() -> new PaymentStatusNotFoundException("PENDING"));

        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .user(user)
                .gateway(gateway)
                .amount(request.getAmount())
                .currency(currency)
                .method(request.getMethod())
                .status(defaultStatus)
                .build();

        payment = paymentRepository.save(payment);

        PaymentResult result = mockPaymentGateway.processPayment(
                request.getOrderId(), request.getAmount(),
                currency.getCode(),
                new HashMap<>());

        if (!result.isSuccess()) {
            payment.setStatus(paymentStatusRepository.findByCode("FAILED")
                    .orElseThrow(() -> new PaymentStatusNotFoundException("FAILED")));
            paymentRepository.save(payment);
            eventPublisher.publishEvent(new PaymentFailedEvent(this, userId, payment.getUuid(), String.valueOf(request.getOrderId())));
            throw new PaymentFailedException(result.getMessage());
        }

        payment.setStatus(paymentStatusRepository.findByCode("COMPLETED")
                .orElseThrow(() -> new PaymentStatusNotFoundException("COMPLETED")));
        payment.setGatewayTransactionId(result.getGatewayTransactionId());
        payment.setPaidAt(Instant.now());
        payment = paymentRepository.save(payment);

        OrderStatus confirmedStatus = orderStatusRepository.findByCode("CONFIRMED")
                .orElseThrow(() -> new OrderStatusNotFoundException("CONFIRMED"));
        order.setStatus(confirmedStatus);
        orderRepository.save(order);

        eventPublisher.publishEvent(new PaymentProcessedEvent(this, userId, payment.getUuid(), String.valueOf(request.getOrderId())));
        return PaymentMapper.toResponse(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getByUuid(String uuid) {
        Payment payment = paymentRepository.findByUuid(uuid)
                .orElseThrow(() -> new PaymentNotFoundException(uuid));
        return PaymentMapper.toResponse(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException("for order: " + orderId));
        return PaymentMapper.toResponse(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getUserPayments(Long userId) {
        return getUserPayments(userId, Pageable.unpaged()).getContent();
    }

    @Transactional(readOnly = true)
    public Page<PaymentResponse> getUserPayments(Long userId, Pageable pageable) {
        return paymentRepository.findByUserId(userId, pageable)
                .map(PaymentMapper::toResponse);
    }

    @Transactional(readOnly = true)
    @RequiresPermission("payment:read")
    public Page<PaymentResponse> listAll(Pageable pageable) {
        return paymentRepository.findAll(pageable).map(PaymentMapper::toResponse);
    }

    @Transactional
    public RefundResponse processRefund(RefundRequest request) {
        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new PaymentNotFoundException("id: " + request.getPaymentId()));

        String currentStatus = payment.getStatus() != null ? payment.getStatus().getCode() : null;
        if (!"COMPLETED".equals(currentStatus) && !"PARTIALLY_REFUNDED".equals(currentStatus)) {
            throw new InvalidRefundException("Only completed payments can be refunded");
        }

        BigDecimal refundedSoFar = refundRepository.findByPaymentIdAndStatus_Code(payment.getId(), "COMPLETED")
                .stream()
                .map(Refund::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal remaining = payment.getAmount().subtract(refundedSoFar);
        if (request.getAmount().compareTo(remaining) > 0) {
            throw new InvalidRefundException(
                    "Refund amount exceeds the remaining refundable amount of " + remaining);
        }

        PaymentResult result = mockPaymentGateway.processRefund(
                payment.getGatewayTransactionId(), request.getAmount(), request.getReason());

        RefundStatus refundStatus = refundStatusRepository.findByCode(result.isSuccess() ? "COMPLETED" : "FAILED")
                .orElseThrow(() -> new RefundStatusNotFoundException(result.isSuccess() ? "COMPLETED" : "FAILED"));

        Refund refund = Refund.builder()
                .payment(payment)
                .returnRequestId(request.getReturnRequestId())
                .amount(request.getAmount())
                .reason(request.getReason())
                .gatewayRefundId(result.getGatewayTransactionId())
                .status(refundStatus)
                .refundedAt(result.isSuccess() ? Instant.now() : null)
                .build();

        refund = refundRepository.save(refund);

        if (result.isSuccess()) {
            BigDecimal totalRefunded = refundedSoFar.add(request.getAmount());
            boolean fullyRefunded = totalRefunded.compareTo(payment.getAmount()) >= 0;
            payment.setStatus(paymentStatusRepository.findByCode(fullyRefunded ? "REFUNDED" : "PARTIALLY_REFUNDED")
                    .orElseThrow(() -> new PaymentStatusNotFoundException(
                            fullyRefunded ? "REFUNDED" : "PARTIALLY_REFUNDED")));
            paymentRepository.save(payment);
        }

        return PaymentMapper.toRefundResponse(refund);
    }

    @Transactional(readOnly = true)
    public List<RefundResponse> getRefundsByPaymentId(Long paymentId) {
        return refundRepository.findByPaymentId(paymentId).stream()
                .map(PaymentMapper::toRefundResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RefundResponse> getRefundsByReturnRequestId(Long returnRequestId) {
        return refundRepository.findByReturnRequestId(returnRequestId).stream()
                .map(PaymentMapper::toRefundResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean refundForReturn(Long orderId, Long returnRequestId, String returnUuid) {
        return paymentRepository.findByOrderId(orderId)
                .filter(payment -> {
                    String code = payment.getStatus() != null ? payment.getStatus().getCode() : null;
                    return "COMPLETED".equals(code) || "PARTIALLY_REFUNDED".equals(code);
                })
                .map(payment -> {
                    BigDecimal refundedSoFar = refundRepository
                            .findByPaymentIdAndStatus_Code(payment.getId(), "COMPLETED")
                            .stream()
                            .map(Refund::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal remaining = payment.getAmount().subtract(refundedSoFar);
                    if (remaining.signum() <= 0) {
                        return false;
                    }
                    RefundRequest request = new RefundRequest();
                    request.setPaymentId(payment.getId());
                    request.setAmount(remaining);
                    request.setReason("Auto refund for approved return request " + returnUuid);
                    request.setReturnRequestId(returnRequestId);
                    processRefund(request);
                    return true;
                })
                .orElse(false);
    }
}
