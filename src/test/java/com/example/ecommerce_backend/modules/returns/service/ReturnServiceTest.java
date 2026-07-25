package com.example.ecommerce_backend.modules.returns.service;

import com.example.ecommerce_backend.modules.order.entity.Order;
import com.example.ecommerce_backend.modules.order.exception.OrderNotFoundException;
import com.example.ecommerce_backend.modules.order.repository.OrderRepository;
import com.example.ecommerce_backend.modules.returns.dto.request.ReturnRequestDto;
import com.example.ecommerce_backend.modules.returns.dto.response.ReturnResponse;
import com.example.ecommerce_backend.modules.returns.entity.*;
import com.example.ecommerce_backend.modules.returns.exception.InvalidReturnStateException;
import com.example.ecommerce_backend.modules.returns.exception.ReturnNotFoundException;
import com.example.ecommerce_backend.modules.returns.exception.ReturnStatusNotFoundException;
import com.example.ecommerce_backend.modules.returns.repository.ReturnConditionRepository;
import com.example.ecommerce_backend.modules.returns.repository.ReturnItemRepository;
import com.example.ecommerce_backend.modules.returns.repository.ReturnRequestRepository;
import com.example.ecommerce_backend.modules.returns.repository.ReturnStatusRepository;
import com.example.ecommerce_backend.modules.returns.repository.ReturnTypeRepository;
import com.example.ecommerce_backend.modules.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReturnServiceTest {

    @Mock
    private ReturnRequestRepository returnRequestRepository;

    @Mock
    private ReturnItemRepository returnItemRepository;

    @Mock
    private ReturnTypeRepository returnTypeRepository;

    @Mock
    private ReturnConditionRepository returnConditionRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ReturnStatusRepository returnStatusRepository;

    @InjectMocks
    private ReturnService returnService;

    private User user;
    private Order order;
    private ReturnStatus pendingStatus;
    private ReturnStatus approvedStatus;
    private ReturnStatus rejectedStatus;
    private ReturnType returnType;
    private ReturnCondition returnCondition;
    private ReturnRequest returnRequest;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).uuid("user-uuid").email("test@test.com").build();
        order = Order.builder().id(1L).uuid("order-uuid").build();
        pendingStatus = ReturnStatus.builder().id(1L).code("PENDING").name("Pending").build();
        approvedStatus = ReturnStatus.builder().id(2L).code("APPROVED").name("Approved").build();
        rejectedStatus = ReturnStatus.builder().id(3L).code("REJECTED").name("Rejected").build();
        returnType = ReturnType.builder().id(1L).uuid("type-uuid").code("REFUND").name("Refund").build();
        returnCondition = ReturnCondition.builder().id(1L).uuid("cond-uuid").code("DAMAGED").name("Damaged").build();
        returnRequest = ReturnRequest.builder()
                .id(1L).uuid("return-uuid")
                .user(user)
                .order(order)
                .status(pendingStatus)
                .reason("Defective")
                .items(List.of())
                .build();
    }

    // --- getAll (no pageable) ---

    @Test
    void getAll_withoutPagination_shouldReturnList() {
        Page<ReturnRequest> page = new PageImpl<>(List.of(returnRequest));
        when(returnRequestRepository.findAll(Pageable.unpaged())).thenReturn(page);

        List<ReturnResponse> result = returnService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUuid()).isEqualTo("return-uuid");
    }

    // --- getAll (with pageable) ---

    @Test
    void getAll_withPagination_shouldReturnPage() {
        Page<ReturnRequest> page = new PageImpl<>(List.of(returnRequest));
        when(returnRequestRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);

        Page<ReturnResponse> result = returnService.getAll(PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
    }

    // --- getByUuid ---

    @Test
    void getByUuid_shouldReturnReturn() {
        when(returnRequestRepository.findByUuid("return-uuid")).thenReturn(Optional.of(returnRequest));

        ReturnResponse result = returnService.getByUuid("return-uuid");

        assertThat(result.getUuid()).isEqualTo("return-uuid");
    }

    @Test
    void getByUuid_whenNotFound_shouldThrow() {
        when(returnRequestRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> returnService.getByUuid("nonexistent"))
                .isInstanceOf(ReturnNotFoundException.class);
    }

    // --- getByUserId (no pageable) ---

    @Test
    void getByUserId_withoutPagination_shouldReturnList() {
        Page<ReturnRequest> page = new PageImpl<>(List.of(returnRequest));
        when(returnRequestRepository.findByUserId(1L, Pageable.unpaged())).thenReturn(page);

        List<ReturnResponse> result = returnService.getByUserId(1L);

        assertThat(result).hasSize(1);
    }

    // --- getByUserId (with pageable) ---

    @Test
    void getByUserId_withPagination_shouldReturnPage() {
        Page<ReturnRequest> page = new PageImpl<>(List.of(returnRequest));
        when(returnRequestRepository.findByUserId(1L, PageRequest.of(0, 10))).thenReturn(page);

        Page<ReturnResponse> result = returnService.getByUserId(1L, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
    }

    // --- create ---

    @Test
    void create_shouldCreateReturnRequest() {
        ReturnRequestDto request = new ReturnRequestDto();
        request.setOrderUuid("order-uuid");
        request.setReason("Defective");

        ReturnRequestDto.ReturnItemDto itemDto = new ReturnRequestDto.ReturnItemDto();
        itemDto.setOrderItemId(1L);
        itemDto.setQuantity(1);
        request.setItems(List.of(itemDto));

        when(orderRepository.findByUuid("order-uuid")).thenReturn(Optional.of(order));
        when(returnStatusRepository.findByCode("PENDING")).thenReturn(Optional.of(pendingStatus));
        when(returnRequestRepository.save(any(ReturnRequest.class))).thenReturn(returnRequest);

        ReturnResponse result = returnService.create(user, request);

        assertThat(result.getUuid()).isEqualTo("return-uuid");
    }

    @Test
    void create_withReturnType_shouldSetType() {
        ReturnRequestDto request = new ReturnRequestDto();
        request.setOrderUuid("order-uuid");
        request.setReturnTypeCode("REFUND");
        request.setReason("Defective");

        ReturnRequestDto.ReturnItemDto itemDto = new ReturnRequestDto.ReturnItemDto();
        itemDto.setOrderItemId(1L);
        itemDto.setQuantity(1);
        request.setItems(List.of(itemDto));

        when(orderRepository.findByUuid("order-uuid")).thenReturn(Optional.of(order));
        when(returnTypeRepository.findByCode("REFUND")).thenReturn(Optional.of(returnType));
        when(returnStatusRepository.findByCode("PENDING")).thenReturn(Optional.of(pendingStatus));
        when(returnRequestRepository.save(any(ReturnRequest.class))).thenReturn(returnRequest);

        returnService.create(user, request);

        verify(returnRequestRepository).save(any(ReturnRequest.class));
    }

    @Test
    void create_withConditionOnItem_shouldSetCondition() {
        ReturnRequestDto request = new ReturnRequestDto();
        request.setOrderUuid("order-uuid");
        request.setReason("Defective");

        ReturnRequestDto.ReturnItemDto itemDto = new ReturnRequestDto.ReturnItemDto();
        itemDto.setOrderItemId(1L);
        itemDto.setQuantity(1);
        itemDto.setConditionCode("DAMAGED");
        request.setItems(List.of(itemDto));

        when(orderRepository.findByUuid("order-uuid")).thenReturn(Optional.of(order));
        when(returnStatusRepository.findByCode("PENDING")).thenReturn(Optional.of(pendingStatus));
        when(returnConditionRepository.findByCode("DAMAGED")).thenReturn(Optional.of(returnCondition));
        when(returnRequestRepository.save(any(ReturnRequest.class))).thenReturn(returnRequest);

        returnService.create(user, request);

        verify(returnRequestRepository).save(any(ReturnRequest.class));
    }

    @Test
    void create_whenOrderNotFound_shouldThrow() {
        ReturnRequestDto request = new ReturnRequestDto();
        request.setOrderUuid("nonexistent");
        request.setReason("Defective");
        request.setItems(List.of());

        when(orderRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> returnService.create(user, request))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void create_whenReturnTypeNotFound_shouldThrow() {
        ReturnRequestDto request = new ReturnRequestDto();
        request.setOrderUuid("order-uuid");
        request.setReturnTypeCode("UNKNOWN");
        request.setReason("Defective");

        ReturnRequestDto.ReturnItemDto itemDto = new ReturnRequestDto.ReturnItemDto();
        itemDto.setOrderItemId(1L);
        itemDto.setQuantity(1);
        request.setItems(List.of(itemDto));

        when(orderRepository.findByUuid("order-uuid")).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> returnService.create(user, request))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void create_whenPendingStatusNotFound_shouldThrow() {
        ReturnRequestDto request = new ReturnRequestDto();
        request.setOrderUuid("order-uuid");
        request.setReason("Defective");

        ReturnRequestDto.ReturnItemDto itemDto = new ReturnRequestDto.ReturnItemDto();
        itemDto.setOrderItemId(1L);
        itemDto.setQuantity(1);
        request.setItems(List.of(itemDto));

        when(orderRepository.findByUuid("order-uuid")).thenReturn(Optional.of(order));
        when(returnStatusRepository.findByCode("PENDING")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> returnService.create(user, request))
                .isInstanceOf(ReturnStatusNotFoundException.class);
    }

    // --- updateStatus ---

    @Test
    void updateStatus_shouldUpdateStatus() {
        when(returnRequestRepository.findByUuid("return-uuid")).thenReturn(Optional.of(returnRequest));
        when(returnStatusRepository.findByCode("APPROVED")).thenReturn(Optional.of(approvedStatus));
        when(returnRequestRepository.save(any(ReturnRequest.class))).thenReturn(returnRequest);

        ReturnResponse result = returnService.updateStatus("return-uuid", "APPROVED", "Approved");

        assertThat(result.getUuid()).isEqualTo("return-uuid");
    }

    @Test
    void updateStatus_whenNotFound_shouldThrow() {
        when(returnRequestRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> returnService.updateStatus("nonexistent", "APPROVED", null))
                .isInstanceOf(ReturnNotFoundException.class);
    }

    @Test
    void updateStatus_whenStatusNotFound_shouldThrow() {
        when(returnRequestRepository.findByUuid("return-uuid")).thenReturn(Optional.of(returnRequest));

        assertThatThrownBy(() -> returnService.updateStatus("return-uuid", "UNKNOWN", null))
                .isInstanceOf(ReturnStatusNotFoundException.class);
    }

    @Test
    void updateStatus_whenAlreadyApproved_shouldThrow() {
        returnRequest.setStatus(approvedStatus);

        when(returnRequestRepository.findByUuid("return-uuid")).thenReturn(Optional.of(returnRequest));

        assertThatThrownBy(() -> returnService.updateStatus("return-uuid", "CLOSED", null))
                .isInstanceOf(InvalidReturnStateException.class);
    }

    @Test
    void updateStatus_whenAlreadyRejected_shouldThrow() {
        returnRequest.setStatus(rejectedStatus);

        when(returnRequestRepository.findByUuid("return-uuid")).thenReturn(Optional.of(returnRequest));

        assertThatThrownBy(() -> returnService.updateStatus("return-uuid", "CLOSED", null))
                .isInstanceOf(InvalidReturnStateException.class);
    }

    // --- delete ---

    @Test
    void delete_shouldDeleteReturn() {
        when(returnRequestRepository.findByUuid("return-uuid")).thenReturn(Optional.of(returnRequest));

        returnService.delete("return-uuid");

        verify(returnRequestRepository).delete(returnRequest);
    }

    @Test
    void delete_whenNotFound_shouldThrow() {
        when(returnRequestRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> returnService.delete("nonexistent"))
                .isInstanceOf(ReturnNotFoundException.class);
    }
}
