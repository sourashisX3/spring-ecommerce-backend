package com.example.ecommerce_backend.modules.payment.service;

import com.example.ecommerce_backend.modules.payment.dto.request.PaymentGatewayRequest;
import com.example.ecommerce_backend.modules.payment.dto.response.PaymentGatewayResponse;
import com.example.ecommerce_backend.modules.payment.entity.PaymentGateway;
import com.example.ecommerce_backend.modules.payment.exception.PaymentGatewayNotFoundException;
import com.example.ecommerce_backend.modules.payment.repository.PaymentGatewayRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentGatewayServiceTest {

    @Mock
    private PaymentGatewayRepository paymentGatewayRepository;

    @InjectMocks
    private PaymentGatewayService paymentGatewayService;

    private PaymentGateway gateway;

    @BeforeEach
    void setUp() {
        gateway = PaymentGateway.builder()
                .id(1L).uuid("gateway-uuid-1").code("STRIPE").name("Stripe")
                .description("Stripe payment gateway")
                .configTemplate("{\"apiKey\": \"sk_test_...\"}")
                .isActive(true)
                .createdAt(Instant.now()).updatedAt(Instant.now())
                .build();
    }

    @Test
    void getAll_shouldReturnAllGateways() {
        when(paymentGatewayRepository.findAll()).thenReturn(List.of(gateway));

        List<PaymentGatewayResponse> result = paymentGatewayService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("STRIPE");
    }

    @Test
    void getByUuid_shouldReturnGateway() {
        when(paymentGatewayRepository.findByUuid("gateway-uuid-1")).thenReturn(Optional.of(gateway));

        PaymentGatewayResponse result = paymentGatewayService.getByUuid("gateway-uuid-1");

        assertThat(result.getCode()).isEqualTo("STRIPE");
    }

    @Test
    void getByUuid_whenNotFound_shouldThrow() {
        when(paymentGatewayRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentGatewayService.getByUuid("nonexistent"))
                .isInstanceOf(PaymentGatewayNotFoundException.class);
    }

    @Test
    void getByCode_shouldReturnGateway() {
        when(paymentGatewayRepository.findByCode("STRIPE")).thenReturn(Optional.of(gateway));

        PaymentGatewayResponse result = paymentGatewayService.getByCode("STRIPE");

        assertThat(result.getCode()).isEqualTo("STRIPE");
    }

    @Test
    void getByCode_whenNotFound_shouldThrow() {
        when(paymentGatewayRepository.findByCode("UNKNOWN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentGatewayService.getByCode("UNKNOWN"))
                .isInstanceOf(PaymentGatewayNotFoundException.class);
    }

    @Test
    void create_shouldSaveAndReturn() {
        when(paymentGatewayRepository.save(any(PaymentGateway.class))).thenAnswer(invocation -> {
            PaymentGateway saved = invocation.getArgument(0);
            saved.setId(1L);
            saved.setUuid("new-gateway-uuid");
            saved.setCreatedAt(Instant.now());
            saved.setUpdatedAt(Instant.now());
            return saved;
        });

        PaymentGatewayRequest request = new PaymentGatewayRequest();
        request.setCode("PAYPAL");
        request.setName("PayPal");
        request.setDescription("PayPal payment gateway");
        request.setConfigTemplate("{\"clientId\": \"...\"}");

        PaymentGatewayResponse result = paymentGatewayService.create(request);

        assertThat(result.getCode()).isEqualTo("PAYPAL");
        assertThat(result.getName()).isEqualTo("PayPal");
        verify(paymentGatewayRepository).save(any(PaymentGateway.class));
    }

    @Test
    void update_shouldModifyAndReturn() {
        when(paymentGatewayRepository.findByUuid("gateway-uuid-1")).thenReturn(Optional.of(gateway));
        when(paymentGatewayRepository.save(any(PaymentGateway.class))).thenReturn(gateway);

        PaymentGatewayRequest request = new PaymentGatewayRequest();
        request.setCode("STRIPE_UPDATED");
        request.setName("Stripe Updated");
        request.setDescription("Updated description");
        request.setConfigTemplate("{}");

        PaymentGatewayResponse result = paymentGatewayService.update("gateway-uuid-1", request);

        assertThat(result.getCode()).isEqualTo("STRIPE_UPDATED");
        verify(paymentGatewayRepository).save(gateway);
    }

    @Test
    void update_whenNotFound_shouldThrow() {
        when(paymentGatewayRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        PaymentGatewayRequest request = new PaymentGatewayRequest();
        request.setCode("X");
        request.setName("Test");

        assertThatThrownBy(() -> paymentGatewayService.update("nonexistent", request))
                .isInstanceOf(PaymentGatewayNotFoundException.class);
    }

    @Test
    void toggleStatus_shouldSetActive() {
        when(paymentGatewayRepository.findByUuid("gateway-uuid-1")).thenReturn(Optional.of(gateway));

        paymentGatewayService.toggleStatus("gateway-uuid-1", false);

        assertThat(gateway.isActive()).isFalse();
        verify(paymentGatewayRepository).save(gateway);
    }

    @Test
    void toggleStatus_whenNotFound_shouldThrow() {
        when(paymentGatewayRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentGatewayService.toggleStatus("nonexistent", true))
                .isInstanceOf(PaymentGatewayNotFoundException.class);
    }

    @Test
    void delete_shouldRemoveGateway() {
        when(paymentGatewayRepository.findByUuid("gateway-uuid-1")).thenReturn(Optional.of(gateway));

        paymentGatewayService.delete("gateway-uuid-1");

        verify(paymentGatewayRepository).delete(gateway);
    }

    @Test
    void delete_whenNotFound_shouldThrow() {
        when(paymentGatewayRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentGatewayService.delete("nonexistent"))
                .isInstanceOf(PaymentGatewayNotFoundException.class);
    }
}
