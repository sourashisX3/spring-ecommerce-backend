package com.example.ecommerce_backend.modules.returns.service;

import com.example.ecommerce_backend.core.exception.BaseException;
import com.example.ecommerce_backend.modules.returns.dto.request.ReturnConditionRequest;
import com.example.ecommerce_backend.modules.returns.dto.response.ReturnConditionResponse;
import com.example.ecommerce_backend.modules.returns.entity.ReturnCondition;
import com.example.ecommerce_backend.modules.returns.repository.ReturnConditionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReturnConditionServiceTest {

    @Mock
    private ReturnConditionRepository returnConditionRepository;

    @InjectMocks
    private ReturnConditionService returnConditionService;

    private ReturnCondition condition;

    @BeforeEach
    void setUp() {
        condition = ReturnCondition.builder()
                .id(1L).uuid("cond-uuid")
                .code("DAMAGED")
                .name("Damaged")
                .description("Item is damaged")
                .isActive(true)
                .build();
    }

    // --- getAll ---

    @Test
    void getAll_shouldReturnAll() {
        when(returnConditionRepository.findAll()).thenReturn(List.of(condition));

        List<ReturnConditionResponse> result = returnConditionService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("DAMAGED");
    }

    @Test
    void getAll_whenEmpty_shouldReturnEmptyList() {
        when(returnConditionRepository.findAll()).thenReturn(List.of());

        List<ReturnConditionResponse> result = returnConditionService.getAll();

        assertThat(result).isEmpty();
    }

    // --- getByUuid ---

    @Test
    void getByUuid_shouldReturnCondition() {
        when(returnConditionRepository.findByUuid("cond-uuid")).thenReturn(Optional.of(condition));

        ReturnConditionResponse result = returnConditionService.getByUuid("cond-uuid");

        assertThat(result.getCode()).isEqualTo("DAMAGED");
    }

    @Test
    void getByUuid_whenNotFound_shouldThrow() {
        when(returnConditionRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> returnConditionService.getByUuid("nonexistent"))
                .isInstanceOf(BaseException.class);
    }

    // --- create ---

    @Test
    void create_shouldCreateCondition() {
        ReturnConditionRequest request = new ReturnConditionRequest();
        request.setCode("OPEN_BOX");
        request.setName("Open Box");
        request.setDescription("Item is open box");

        when(returnConditionRepository.findByCode("OPEN_BOX")).thenReturn(Optional.empty());
        when(returnConditionRepository.save(any(ReturnCondition.class))).thenReturn(condition);

        ReturnConditionResponse result = returnConditionService.create(request);

        assertThat(result.getCode()).isEqualTo("DAMAGED");
    }

    @Test
    void create_whenCodeExists_shouldThrow() {
        ReturnConditionRequest request = new ReturnConditionRequest();
        request.setCode("DAMAGED");
        request.setName("Damaged");

        when(returnConditionRepository.findByCode("DAMAGED")).thenReturn(Optional.of(condition));

        assertThatThrownBy(() -> returnConditionService.create(request))
                .isInstanceOf(BaseException.class);
    }

    // --- update ---

    @Test
    void update_shouldUpdateCondition() {
        ReturnConditionRequest request = new ReturnConditionRequest();
        request.setCode("DAMAGED");
        request.setName("Damaged Item");
        request.setDescription("Item is severely damaged");

        when(returnConditionRepository.findByUuid("cond-uuid")).thenReturn(Optional.of(condition));
        when(returnConditionRepository.save(any(ReturnCondition.class))).thenReturn(condition);

        ReturnConditionResponse result = returnConditionService.update("cond-uuid", request);

        assertThat(result.getName()).isEqualTo("Damaged Item");
    }

    @Test
    void update_whenNotFound_shouldThrow() {
        ReturnConditionRequest request = new ReturnConditionRequest();
        request.setCode("DAMAGED");
        request.setName("Damaged");

        when(returnConditionRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> returnConditionService.update("nonexistent", request))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void update_whenCodeConflicts_shouldThrow() {
        ReturnCondition other = ReturnCondition.builder()
                .id(2L).uuid("other-cond")
                .code("OPEN_BOX")
                .name("Open Box")
                .build();

        ReturnConditionRequest request = new ReturnConditionRequest();
        request.setCode("OPEN_BOX");
        request.setName("Damaged");

        when(returnConditionRepository.findByUuid("cond-uuid")).thenReturn(Optional.of(condition));
        when(returnConditionRepository.findByCode("OPEN_BOX")).thenReturn(Optional.of(other));

        assertThatThrownBy(() -> returnConditionService.update("cond-uuid", request))
                .isInstanceOf(BaseException.class);
    }

    // --- delete ---

    @Test
    void delete_shouldDeleteCondition() {
        when(returnConditionRepository.findByUuid("cond-uuid")).thenReturn(Optional.of(condition));

        returnConditionService.delete("cond-uuid");

        verify(returnConditionRepository).delete(condition);
    }

    @Test
    void delete_whenNotFound_shouldThrow() {
        when(returnConditionRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> returnConditionService.delete("nonexistent"))
                .isInstanceOf(BaseException.class);
    }
}
