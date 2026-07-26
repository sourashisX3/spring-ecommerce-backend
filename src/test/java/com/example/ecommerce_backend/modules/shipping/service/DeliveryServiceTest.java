package com.example.ecommerce_backend.modules.shipping.service;

import com.example.ecommerce_backend.core.event.DeliveryStatusChangedEvent;
import com.example.ecommerce_backend.modules.shipping.dto.request.DeliveryRequest;
import com.example.ecommerce_backend.modules.shipping.exception.ShippingCarrierNotActiveException;
import com.example.ecommerce_backend.modules.shipping.dto.request.UpdateDeliveryRequest;
import com.example.ecommerce_backend.modules.shipping.dto.response.DeliveryResponse;
import com.example.ecommerce_backend.modules.shipping.entity.Delivery;
import com.example.ecommerce_backend.modules.shipping.entity.DeliveryStatus;
import com.example.ecommerce_backend.modules.shipping.entity.ShippingAddress;
import com.example.ecommerce_backend.modules.shipping.entity.ShippingCarrier;
import com.example.ecommerce_backend.modules.shipping.exception.AddressNotFoundException;
import com.example.ecommerce_backend.modules.shipping.exception.DeliveryNotFoundException;
import com.example.ecommerce_backend.modules.shipping.repository.DeliveryRepository;
import com.example.ecommerce_backend.modules.shipping.repository.DeliveryStatusRepository;
import com.example.ecommerce_backend.modules.shipping.repository.ShippingAddressRepository;
import com.example.ecommerce_backend.modules.shipping.repository.ShippingCarrierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private ShippingAddressRepository shippingAddressRepository;

    @Mock
    private ShippingCarrierRepository shippingCarrierRepository;

    @Mock
    private DeliveryStatusRepository deliveryStatusRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private DeliveryService deliveryService;

    @Captor
    private ArgumentCaptor<DeliveryStatusChangedEvent> eventCaptor;

    private ShippingAddress address;
    private ShippingCarrier carrier;
    private DeliveryStatus pendingStatus;
    private DeliveryStatus shippedStatus;
    private Delivery delivery;

    @BeforeEach
    void setUp() {
        address = ShippingAddress.builder()
                .id(1L).uuid("addr-uuid")
                .recipientName("John Doe")
                .build();
        carrier = ShippingCarrier.builder()
                .id(1L).uuid("carrier-uuid")
                .code("UPS").name("UPS")
                .isActive(true)
                .build();
        pendingStatus = DeliveryStatus.builder().id(1L).code("PENDING").name("Pending").build();
        shippedStatus = DeliveryStatus.builder().id(2L).code("SHIPPED").name("Shipped").build();
        delivery = Delivery.builder()
                .id(1L).uuid("delivery-uuid")
                .orderId(1L)
                .shippingAddress(address)
                .carrier(carrier)
                .status(pendingStatus)
                .trackingNumber("TRACK123")
                .build();
    }

    // --- getByOrderId ---

    @Test
    void getByOrderId_shouldReturnDeliveries() {
        when(deliveryRepository.findByOrderId(1L)).thenReturn(List.of(delivery));

        List<DeliveryResponse> result = deliveryService.getByOrderId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUuid()).isEqualTo("delivery-uuid");
    }

    @Test
    void getByOrderId_whenEmpty_shouldReturnEmptyList() {
        when(deliveryRepository.findByOrderId(1L)).thenReturn(List.of());

        List<DeliveryResponse> result = deliveryService.getByOrderId(1L);

        assertThat(result).isEmpty();
    }

    // --- createDelivery ---

    @Test
    void createDelivery_shouldCreateAndReturn() {
        DeliveryRequest request = new DeliveryRequest();
        request.setShippingAddressId(1L);
        request.setCarrierCode("UPS");
        request.setTrackingNumber("TRACK123");

        when(shippingAddressRepository.findById(1L)).thenReturn(Optional.of(address));
        when(shippingCarrierRepository.findByCode("UPS")).thenReturn(Optional.of(carrier));
        when(deliveryStatusRepository.findByCode("PENDING")).thenReturn(Optional.of(pendingStatus));
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(delivery);

        DeliveryResponse result = deliveryService.createDelivery(1L, request);

        assertThat(result.getUuid()).isEqualTo("delivery-uuid");
        assertThat(result.getTrackingNumber()).isEqualTo("TRACK123");
    }

    @Test
    void createDelivery_whenAddressNotFound_shouldThrow() {
        DeliveryRequest request = new DeliveryRequest();
        request.setShippingAddressId(999L);
        request.setCarrierCode("UPS");

        when(shippingAddressRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deliveryService.createDelivery(1L, request))
                .isInstanceOf(AddressNotFoundException.class);
    }

    @Test
    void createDelivery_whenCarrierNotFound_shouldThrow() {
        DeliveryRequest request = new DeliveryRequest();
        request.setShippingAddressId(1L);
        request.setCarrierCode("UNKNOWN");

        when(shippingAddressRepository.findById(1L)).thenReturn(Optional.of(address));
        when(shippingCarrierRepository.findByCode("UNKNOWN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deliveryService.createDelivery(1L, request))
                .isInstanceOf(DeliveryNotFoundException.class);
    }

    @Test
    void createDelivery_whenCarrierInactive_shouldThrow() {
        carrier.setActive(false);
        DeliveryRequest request = new DeliveryRequest();
        request.setShippingAddressId(1L);
        request.setCarrierCode("UPS");

        when(shippingAddressRepository.findById(1L)).thenReturn(Optional.of(address));
        when(shippingCarrierRepository.findByCode("UPS")).thenReturn(Optional.of(carrier));

        assertThatThrownBy(() -> deliveryService.createDelivery(1L, request))
                .isInstanceOf(ShippingCarrierNotActiveException.class);
    }

    @Test
    void createDelivery_whenPendingStatusNotFound_shouldThrow() {
        DeliveryRequest request = new DeliveryRequest();
        request.setShippingAddressId(1L);
        request.setCarrierCode("UPS");

        when(shippingAddressRepository.findById(1L)).thenReturn(Optional.of(address));
        when(shippingCarrierRepository.findByCode("UPS")).thenReturn(Optional.of(carrier));
        when(deliveryStatusRepository.findByCode("PENDING")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deliveryService.createDelivery(1L, request))
                .isInstanceOf(DeliveryNotFoundException.class);
    }

    // --- updateDelivery ---

    @Test
    void updateDelivery_shouldUpdateFieldsAndPublishEvent() {
        UpdateDeliveryRequest request = new UpdateDeliveryRequest();
        request.setCarrierCode("UPS");
        request.setTrackingNumber("TRACK456");
        request.setStatus("SHIPPED");

        when(deliveryRepository.findByUuid("delivery-uuid")).thenReturn(Optional.of(delivery));
        when(shippingCarrierRepository.findByCode("UPS")).thenReturn(Optional.of(carrier));
        when(deliveryStatusRepository.findByCode("SHIPPED")).thenReturn(Optional.of(shippedStatus));
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(delivery);

        DeliveryResponse result = deliveryService.updateDelivery("delivery-uuid", request);

        assertThat(result.getUuid()).isEqualTo("delivery-uuid");
        assertThat(delivery.getTrackingNumber()).isEqualTo("TRACK456");
        assertThat(delivery.getStatus().getCode()).isEqualTo("SHIPPED");
        assertThat(delivery.getShippedAt()).isNotNull();

        verify(eventPublisher).publishEvent(any(DeliveryStatusChangedEvent.class));
    }

    @Test
    void updateDelivery_whenNotFound_shouldThrow() {
        UpdateDeliveryRequest request = new UpdateDeliveryRequest();

        when(deliveryRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deliveryService.updateDelivery("nonexistent", request))
                .isInstanceOf(DeliveryNotFoundException.class);
    }

    @Test
    void updateDelivery_whenCarrierNotFound_shouldThrow() {
        UpdateDeliveryRequest request = new UpdateDeliveryRequest();
        request.setCarrierCode("UNKNOWN");

        when(deliveryRepository.findByUuid("delivery-uuid")).thenReturn(Optional.of(delivery));
        when(shippingCarrierRepository.findByCode("UNKNOWN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deliveryService.updateDelivery("delivery-uuid", request))
                .isInstanceOf(DeliveryNotFoundException.class);
    }

    @Test
    void updateDelivery_whenCarrierInactive_shouldThrow() {
        UpdateDeliveryRequest request = new UpdateDeliveryRequest();
        request.setCarrierCode("UPS");
        carrier.setActive(false);

        when(deliveryRepository.findByUuid("delivery-uuid")).thenReturn(Optional.of(delivery));
        when(shippingCarrierRepository.findByCode("UPS")).thenReturn(Optional.of(carrier));

        assertThatThrownBy(() -> deliveryService.updateDelivery("delivery-uuid", request))
                .isInstanceOf(ShippingCarrierNotActiveException.class);
    }

    @Test
    void updateDelivery_whenStatusNotFound_shouldThrow() {
        UpdateDeliveryRequest request = new UpdateDeliveryRequest();
        request.setStatus("UNKNOWN");

        when(deliveryRepository.findByUuid("delivery-uuid")).thenReturn(Optional.of(delivery));
        when(deliveryStatusRepository.findByCode("UNKNOWN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deliveryService.updateDelivery("delivery-uuid", request))
                .isInstanceOf(DeliveryNotFoundException.class);
    }

    @Test
    void updateDelivery_withDeliveredStatus_shouldSetDeliveredAt() {
        DeliveryStatus deliveredStatus = DeliveryStatus.builder().id(3L).code("DELIVERED").name("Delivered").build();
        UpdateDeliveryRequest request = new UpdateDeliveryRequest();
        request.setStatus("DELIVERED");

        when(deliveryRepository.findByUuid("delivery-uuid")).thenReturn(Optional.of(delivery));
        when(deliveryStatusRepository.findByCode("DELIVERED")).thenReturn(Optional.of(deliveredStatus));
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(delivery);

        deliveryService.updateDelivery("delivery-uuid", request);

        assertThat(delivery.getDeliveredAt()).isNotNull();
    }
}
