package com.example.ecommerce_backend.modules.payment.service;

import com.example.ecommerce_backend.core.event.PaymentFailedEvent;
import com.example.ecommerce_backend.core.event.PaymentProcessedEvent;
import com.example.ecommerce_backend.modules.currency.entity.Currency;
import com.example.ecommerce_backend.modules.currency.repository.CurrencyRepository;
import com.example.ecommerce_backend.modules.order.entity.Order;
import com.example.ecommerce_backend.modules.order.entity.OrderStatus;
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
import org.springframework.data.domain.Pageable;
import com.example.ecommerce_backend.modules.payment.exception.PaymentFailedException;
import com.example.ecommerce_backend.modules.payment.exception.PaymentGatewayNotFoundException;
import com.example.ecommerce_backend.modules.payment.exception.PaymentNotFoundException;
import com.example.ecommerce_backend.modules.payment.repository.PaymentGatewayRepository;
import com.example.ecommerce_backend.modules.payment.repository.PaymentRepository;
import com.example.ecommerce_backend.modules.payment.repository.PaymentStatusRepository;
import com.example.ecommerce_backend.modules.payment.repository.RefundRepository;
import com.example.ecommerce_backend.modules.payment.repository.RefundStatusRepository;
import com.example.ecommerce_backend.modules.user.entity.User;
import com.example.ecommerce_backend.modules.user.exception.UserNotFoundException;
import com.example.ecommerce_backend.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentGatewayRepository paymentGatewayRepository;

    @Mock
    private RefundRepository refundRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderStatusRepository orderStatusRepository;

    @Mock
    private PaymentStatusRepository paymentStatusRepository;

    @Mock
    private RefundStatusRepository refundStatusRepository;

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private MockPaymentGateway mockPaymentGateway;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PaymentService paymentService;

    private User user;
    private PaymentGateway gateway;
    private Currency usdCurrency;
    private PaymentStatus pendingStatus;
    private PaymentStatus completedStatus;
    private PaymentStatus failedStatus;
    private PaymentStatus refundedStatus;
    private RefundStatus refundCompletedStatus;
    private RefundStatus refundFailedStatus;
    private OrderStatus confirmedStatus;
    private Order order;
    private Payment payment;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("test@example.com").firstName("Test").lastName("User").password("pass").isActive(true).build();

        gateway = PaymentGateway.builder()
                .id(1L).uuid("gateway-uuid").code("STRIPE").name("Stripe").description("Stripe gateway")
                .isActive(true).build();

        usdCurrency = Currency.builder().id(1L).code("USD").name("US Dollar").symbol("$").build();

        pendingStatus = PaymentStatus.builder().id(1L).code("PENDING").name("Pending").build();
        completedStatus = PaymentStatus.builder().id(2L).code("COMPLETED").name("Completed").build();
        failedStatus = PaymentStatus.builder().id(3L).code("FAILED").name("Failed").build();
        refundedStatus = PaymentStatus.builder().id(4L).code("REFUNDED").name("Refunded").build();

        refundCompletedStatus = RefundStatus.builder().id(1L).code("COMPLETED").name("Completed").build();
        refundFailedStatus = RefundStatus.builder().id(2L).code("FAILED").name("Failed").build();

        confirmedStatus = OrderStatus.builder().id(3L).code("CONFIRMED").name("Confirmed").build();

        order = Order.builder()
                .id(100L).uuid("order-uuid").orderNumber("ORD-123")
                .user(user)
                .subtotal(BigDecimal.valueOf(100)).total(BigDecimal.valueOf(100))
                .currency(usdCurrency)
                .build();

        payment = Payment.builder()
                .id(1L).uuid("payment-uuid-1").orderId(100L)
                .user(user).gateway(gateway)
                .amount(BigDecimal.valueOf(99.99)).currency(usdCurrency)
                .status(completedStatus).method("credit_card")
                .gatewayTransactionId("TXN-123456")
                .paidAt(Instant.now())
                .createdAt(Instant.now()).updatedAt(Instant.now())
                .build();
    }

    @Test
    void processPayment_shouldProcessSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(paymentGatewayRepository.findByCode("STRIPE")).thenReturn(Optional.of(gateway));
        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));
        when(currencyRepository.findByCode("USD")).thenReturn(Optional.of(usdCurrency));
        when(paymentStatusRepository.findByCode("PENDING")).thenReturn(Optional.of(pendingStatus));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            p.setId(1L);
            p.setUuid("payment-uuid-1");
            p.setCreatedAt(Instant.now());
            p.setUpdatedAt(Instant.now());
            return p;
        });
        when(mockPaymentGateway.processPayment(100L, BigDecimal.valueOf(99.99), "USD", new java.util.HashMap<>()))
                .thenReturn(new PaymentResult(true, "TXN-123456", "Success"));
        when(paymentStatusRepository.findByCode("COMPLETED")).thenReturn(Optional.of(completedStatus));
        when(orderStatusRepository.findByCode("CONFIRMED")).thenReturn(Optional.of(confirmedStatus));

        PaymentRequest request = new PaymentRequest();
        request.setOrderId(100L);
        request.setGatewayCode("STRIPE");
        request.setAmount(BigDecimal.valueOf(99.99));
        request.setCurrency("USD");
        request.setMethod("credit_card");

        PaymentResponse result = paymentService.processPayment(request, 1L);

        assertThat(result.getUuid()).isEqualTo("payment-uuid-1");
        assertThat(result.getStatus()).isEqualTo("COMPLETED");
        verify(eventPublisher).publishEvent(any(PaymentProcessedEvent.class));
    }

    @Test
    void processPayment_whenGatewayFails_shouldThrowAndSetFailed() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(paymentGatewayRepository.findByCode("STRIPE")).thenReturn(Optional.of(gateway));
        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));
        when(currencyRepository.findByCode("USD")).thenReturn(Optional.of(usdCurrency));
        when(paymentStatusRepository.findByCode("PENDING")).thenReturn(Optional.of(pendingStatus));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            p.setId(1L);
            p.setUuid("payment-uuid-1");
            return p;
        });
        when(mockPaymentGateway.processPayment(100L, BigDecimal.valueOf(99.99), "USD", new java.util.HashMap<>()))
                .thenReturn(new PaymentResult(false, null, "Card declined"));
        when(paymentStatusRepository.findByCode("FAILED")).thenReturn(Optional.of(failedStatus));

        PaymentRequest request = new PaymentRequest();
        request.setOrderId(100L);
        request.setGatewayCode("STRIPE");
        request.setAmount(BigDecimal.valueOf(99.99));
        request.setCurrency("USD");

        assertThatThrownBy(() -> paymentService.processPayment(request, 1L))
                .isInstanceOf(PaymentFailedException.class)
                .hasMessageContaining("Card declined");

        verify(paymentRepository, times(2)).save(any(Payment.class));
        verify(eventPublisher).publishEvent(any(PaymentFailedEvent.class));
    }

    @Test
    void processPayment_whenUserNotFound_shouldThrow() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        PaymentRequest request = new PaymentRequest();
        request.setOrderId(100L);
        request.setGatewayCode("STRIPE");
        request.setAmount(BigDecimal.TEN);

        assertThatThrownBy(() -> paymentService.processPayment(request, 99L))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void processPayment_whenGatewayNotFound_shouldThrow() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(paymentGatewayRepository.findByCode("INVALID")).thenReturn(Optional.empty());

        PaymentRequest request = new PaymentRequest();
        request.setOrderId(100L);
        request.setGatewayCode("INVALID");
        request.setAmount(BigDecimal.TEN);

        assertThatThrownBy(() -> paymentService.processPayment(request, 1L))
                .isInstanceOf(PaymentGatewayNotFoundException.class);
    }

    @Test
    void getByUuid_shouldReturnPayment() {
        when(paymentRepository.findByUuid("payment-uuid-1")).thenReturn(Optional.of(payment));

        PaymentResponse result = paymentService.getByUuid("payment-uuid-1");

        assertThat(result.getUuid()).isEqualTo("payment-uuid-1");
    }

    @Test
    void getByUuid_whenNotFound_shouldThrow() {
        when(paymentRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.getByUuid("nonexistent"))
                .isInstanceOf(PaymentNotFoundException.class);
    }

    @Test
    void getByOrderId_shouldReturnPayment() {
        when(paymentRepository.findByOrderId(100L)).thenReturn(Optional.of(payment));

        PaymentResponse result = paymentService.getByOrderId(100L);

        assertThat(result.getUuid()).isEqualTo("payment-uuid-1");
    }

    @Test
    void getByOrderId_whenNotFound_shouldThrow() {
        when(paymentRepository.findByOrderId(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.getByOrderId(999L))
                .isInstanceOf(PaymentNotFoundException.class);
    }

    @Test
    void getUserPayments_withPageable_shouldReturnPage() {
        PageRequest pageable = PageRequest.of(0, 20);
        when(paymentRepository.findByUserId(1L, pageable)).thenReturn(new PageImpl<>(List.of(payment)));

        Page<PaymentResponse> result = paymentService.getUserPayments(1L, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUuid()).isEqualTo("payment-uuid-1");
    }

    @Test
    void getUserPayments_withoutPageable_shouldReturnList() {
        when(paymentRepository.findByUserId(1L, Pageable.unpaged())).thenReturn(new PageImpl<>(List.of(payment)));

        List<PaymentResponse> result = paymentService.getUserPayments(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUuid()).isEqualTo("payment-uuid-1");
    }

    @Test
    void processRefund_shouldProcessSuccessfully() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(mockPaymentGateway.processRefund("TXN-123456", BigDecimal.TEN, "Damaged item"))
                .thenReturn(new PaymentResult(true, "RFD-123456", "Refund processed"));
        when(refundStatusRepository.findByCode("COMPLETED")).thenReturn(Optional.of(refundCompletedStatus));
        when(refundRepository.save(any(Refund.class))).thenAnswer(invocation -> {
            Refund r = invocation.getArgument(0);
            r.setId(1L);
            r.setUuid("refund-uuid-1");
            r.setCreatedAt(Instant.now());
            r.setUpdatedAt(Instant.now());
            return r;
        });
        when(paymentStatusRepository.findByCode("REFUNDED")).thenReturn(Optional.of(refundedStatus));

        RefundRequest request = new RefundRequest();
        request.setPaymentId(1L);
        request.setAmount(BigDecimal.TEN);
        request.setReason("Damaged item");
        request.setReturnRequestId(5L);

        RefundResponse result = paymentService.processRefund(request);

        assertThat(result.getAmount()).isEqualByComparingTo(BigDecimal.TEN);
        assertThat(result.getGatewayRefundId()).isEqualTo("RFD-123456");
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void processRefund_whenGatewayFails_shouldSaveFailedRefund() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(mockPaymentGateway.processRefund("TXN-123456", BigDecimal.TEN, "Cannot process"))
                .thenReturn(new PaymentResult(false, null, "Refund rejected"));
        when(refundStatusRepository.findByCode("FAILED")).thenReturn(Optional.of(refundFailedStatus));
        when(refundRepository.save(any(Refund.class))).thenAnswer(invocation -> {
            Refund r = invocation.getArgument(0);
            r.setId(1L);
            r.setUuid("refund-uuid-1");
            return r;
        });

        RefundRequest request = new RefundRequest();
        request.setPaymentId(1L);
        request.setAmount(BigDecimal.TEN);
        request.setReason("Cannot process");

        RefundResponse result = paymentService.processRefund(request);

        assertThat(result.getStatus()).isEqualTo("FAILED");
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void processRefund_whenPaymentNotFound_shouldThrow() {
        when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

        RefundRequest request = new RefundRequest();
        request.setPaymentId(999L);
        request.setAmount(BigDecimal.TEN);

        assertThatThrownBy(() -> paymentService.processRefund(request))
                .isInstanceOf(PaymentNotFoundException.class);
    }

    @Test
    void getRefundsByPaymentId_shouldReturnList() {
        Refund refund = Refund.builder()
                .id(1L).uuid("refund-uuid-1")
                .payment(payment).amount(BigDecimal.TEN)
                .reason("Damaged").status(refundCompletedStatus)
                .gatewayRefundId("RFD-123456")
                .refundedAt(Instant.now())
                .createdAt(Instant.now()).updatedAt(Instant.now())
                .build();

        when(refundRepository.findByPaymentId(1L)).thenReturn(List.of(refund));

        List<RefundResponse> result = paymentService.getRefundsByPaymentId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUuid()).isEqualTo("refund-uuid-1");
    }
}
