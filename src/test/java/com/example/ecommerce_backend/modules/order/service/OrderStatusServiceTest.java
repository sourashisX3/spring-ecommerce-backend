package com.example.ecommerce_backend.modules.order.service;

import com.example.ecommerce_backend.modules.order.dto.request.OrderStatusRequest;
import com.example.ecommerce_backend.modules.order.dto.response.OrderStatusResponse;
import com.example.ecommerce_backend.modules.order.entity.OrderStatus;
import com.example.ecommerce_backend.modules.order.entity.OrderStatusTransition;
import com.example.ecommerce_backend.modules.order.repository.OrderStatusRepository;
import com.example.ecommerce_backend.modules.order.repository.OrderStatusTransitionRepository;
import com.example.ecommerce_backend.modules.role.entity.Role;
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
class OrderStatusServiceTest {

    @Mock
    private OrderStatusRepository orderStatusRepository;

    @Mock
    private OrderStatusTransitionRepository orderStatusTransitionRepository;

    @InjectMocks
    private OrderStatusService orderStatusService;

    private OrderStatus pendingStatus;
    private OrderStatus confirmedStatus;

    @BeforeEach
    void setUp() {
        pendingStatus = OrderStatus.builder()
                .id(1L).uuid("uuid-pending").code("PENDING").name("Pending")
                .description("Order is pending").sortOrder(1).isActive(true)
                .createdAt(Instant.now()).updatedAt(Instant.now())
                .build();

        confirmedStatus = OrderStatus.builder()
                .id(2L).uuid("uuid-confirmed").code("CONFIRMED").name("Confirmed")
                .description("Order is confirmed").sortOrder(2).isActive(true)
                .createdAt(Instant.now()).updatedAt(Instant.now())
                .build();
    }

    @Test
    void getAll_shouldReturnAllStatusesOrdered() {
        when(orderStatusRepository.findAllByOrderBySortOrderAsc()).thenReturn(List.of(pendingStatus, confirmedStatus));

        List<OrderStatusResponse> result = orderStatusService.getAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCode()).isEqualTo("PENDING");
        assertThat(result.get(1).getCode()).isEqualTo("CONFIRMED");
    }

    @Test
    void getByUuid_shouldReturnStatus() {
        when(orderStatusRepository.findByUuid("uuid-pending")).thenReturn(Optional.of(pendingStatus));

        OrderStatusResponse result = orderStatusService.getByUuid("uuid-pending");

        assertThat(result.getCode()).isEqualTo("PENDING");
    }

    @Test
    void getByUuid_whenNotFound_shouldThrow() {
        when(orderStatusRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderStatusService.getByUuid("nonexistent"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getByCode_shouldReturnStatus() {
        when(orderStatusRepository.findByCode("PENDING")).thenReturn(Optional.of(pendingStatus));

        OrderStatusResponse result = orderStatusService.getByCode("PENDING");

        assertThat(result.getCode()).isEqualTo("PENDING");
    }

    @Test
    void getByCode_whenNotFound_shouldThrow() {
        when(orderStatusRepository.findByCode("UNKNOWN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderStatusService.getByCode("UNKNOWN"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void create_shouldSaveAndReturn() {
        when(orderStatusRepository.save(any(OrderStatus.class))).thenAnswer(invocation -> {
            OrderStatus saved = invocation.getArgument(0);
            saved.setId(1L);
            saved.setUuid("uuid-new");
            saved.setCreatedAt(Instant.now());
            saved.setUpdatedAt(Instant.now());
            return saved;
        });

        OrderStatusRequest request = new OrderStatusRequest();
        request.setCode("NEW_STATUS");
        request.setName("New Status");
        request.setDescription("A new status");
        request.setSortOrder(3);

        OrderStatusResponse result = orderStatusService.create(request);

        assertThat(result.getCode()).isEqualTo("NEW_STATUS");
        assertThat(result.getSortOrder()).isEqualTo(3);
        verify(orderStatusRepository).save(any(OrderStatus.class));
    }

    @Test
    void update_shouldModifyAndReturn() {
        when(orderStatusRepository.findByUuid("uuid-pending")).thenReturn(Optional.of(pendingStatus));
        when(orderStatusRepository.save(any(OrderStatus.class))).thenReturn(pendingStatus);

        OrderStatusRequest request = new OrderStatusRequest();
        request.setCode("UPDATED");
        request.setName("Updated");
        request.setDescription("Updated description");
        request.setSortOrder(5);

        OrderStatusResponse result = orderStatusService.update("uuid-pending", request);

        assertThat(result.getCode()).isEqualTo("UPDATED");
        verify(orderStatusRepository).save(pendingStatus);
    }

    @Test
    void update_whenNotFound_shouldThrow() {
        when(orderStatusRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        OrderStatusRequest request = new OrderStatusRequest();
        request.setCode("X");

        assertThatThrownBy(() -> orderStatusService.update("nonexistent", request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void toggleStatus_shouldSetActive() {
        when(orderStatusRepository.findByUuid("uuid-pending")).thenReturn(Optional.of(pendingStatus));
        when(orderStatusRepository.save(any(OrderStatus.class))).thenReturn(pendingStatus);

        OrderStatusResponse result = orderStatusService.toggleStatus("uuid-pending", false);

        assertThat(result.isActive()).isFalse();
        verify(orderStatusRepository).save(pendingStatus);
    }

    @Test
    void toggleStatus_whenNotFound_shouldThrow() {
        when(orderStatusRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderStatusService.toggleStatus("nonexistent", true))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void delete_shouldRemoveStatus() {
        when(orderStatusRepository.findByUuid("uuid-pending")).thenReturn(Optional.of(pendingStatus));

        orderStatusService.delete("uuid-pending");

        verify(orderStatusRepository).delete(pendingStatus);
    }

    @Test
    void delete_whenNotFound_shouldThrow() {
        when(orderStatusRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderStatusService.delete("nonexistent"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void isValidTransition_whenFromCodeIsNull_shouldReturnTrue() {
        boolean valid = orderStatusService.isValidTransition(null, "PENDING", "USER");

        assertThat(valid).isTrue();
        verify(orderStatusTransitionRepository, never()).findByFromStatus_CodeAndToStatus_CodeAndAllowedBy_RoleName(any(), any(), any());
    }

    @Test
    void isValidTransition_whenTransitionExists_shouldReturnTrue() {
        Role role = Role.builder().id(1L).roleName("ADMIN").build();
        OrderStatusTransition transition = OrderStatusTransition.builder()
                .id(1L).fromStatus(pendingStatus).toStatus(confirmedStatus).allowedBy(role).build();

        when(orderStatusTransitionRepository
                .findByFromStatus_CodeAndToStatus_CodeAndAllowedBy_RoleName("PENDING", "CONFIRMED", "ADMIN"))
                .thenReturn(Optional.of(transition));

        boolean valid = orderStatusService.isValidTransition("PENDING", "CONFIRMED", "ADMIN");

        assertThat(valid).isTrue();
    }

    @Test
    void isValidTransition_whenTransitionNotFound_shouldReturnFalse() {
        when(orderStatusTransitionRepository
                .findByFromStatus_CodeAndToStatus_CodeAndAllowedBy_RoleName("PENDING", "CANCELLED", "USER"))
                .thenReturn(Optional.empty());

        boolean valid = orderStatusService.isValidTransition("PENDING", "CANCELLED", "USER");

        assertThat(valid).isFalse();
    }
}
