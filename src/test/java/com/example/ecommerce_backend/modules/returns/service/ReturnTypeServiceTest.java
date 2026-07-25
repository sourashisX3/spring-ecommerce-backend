package com.example.ecommerce_backend.modules.returns.service;

import com.example.ecommerce_backend.core.exception.BaseException;
import com.example.ecommerce_backend.modules.returns.dto.request.ReturnTypeRequest;
import com.example.ecommerce_backend.modules.returns.dto.response.ReturnTypeResponse;
import com.example.ecommerce_backend.modules.returns.entity.ReturnType;
import com.example.ecommerce_backend.modules.returns.repository.ReturnTypeRepository;
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
class ReturnTypeServiceTest {

    @Mock
    private ReturnTypeRepository returnTypeRepository;

    @InjectMocks
    private ReturnTypeService returnTypeService;

    private ReturnType returnType;

    @BeforeEach
    void setUp() {
        returnType = ReturnType.builder()
                .id(1L).uuid("type-uuid")
                .code("REFUND")
                .name("Refund")
                .description("Full refund")
                .isActive(true)
                .build();
    }

    // --- getAll ---

    @Test
    void getAll_shouldReturnAll() {
        when(returnTypeRepository.findAll()).thenReturn(List.of(returnType));

        List<ReturnTypeResponse> result = returnTypeService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("REFUND");
    }

    @Test
    void getAll_whenEmpty_shouldReturnEmptyList() {
        when(returnTypeRepository.findAll()).thenReturn(List.of());

        List<ReturnTypeResponse> result = returnTypeService.getAll();

        assertThat(result).isEmpty();
    }

    // --- getByUuid ---

    @Test
    void getByUuid_shouldReturnType() {
        when(returnTypeRepository.findByUuid("type-uuid")).thenReturn(Optional.of(returnType));

        ReturnTypeResponse result = returnTypeService.getByUuid("type-uuid");

        assertThat(result.getCode()).isEqualTo("REFUND");
    }

    @Test
    void getByUuid_whenNotFound_shouldThrow() {
        when(returnTypeRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> returnTypeService.getByUuid("nonexistent"))
                .isInstanceOf(BaseException.class);
    }

    // --- create ---

    @Test
    void create_shouldCreateType() {
        ReturnTypeRequest request = new ReturnTypeRequest();
        request.setCode("EXCHANGE");
        request.setName("Exchange");
        request.setDescription("Exchange for new item");

        when(returnTypeRepository.findByCode("EXCHANGE")).thenReturn(Optional.empty());
        when(returnTypeRepository.save(any(ReturnType.class))).thenReturn(returnType);

        ReturnTypeResponse result = returnTypeService.create(request);

        assertThat(result.getCode()).isEqualTo("REFUND");
    }

    @Test
    void create_whenCodeExists_shouldThrow() {
        ReturnTypeRequest request = new ReturnTypeRequest();
        request.setCode("REFUND");
        request.setName("Refund");

        when(returnTypeRepository.findByCode("REFUND")).thenReturn(Optional.of(returnType));

        assertThatThrownBy(() -> returnTypeService.create(request))
                .isInstanceOf(BaseException.class);
    }

    // --- update ---

    @Test
    void update_shouldUpdateType() {
        ReturnTypeRequest request = new ReturnTypeRequest();
        request.setCode("REFUND");
        request.setName("Full Refund");
        request.setDescription("Full refund to original payment");

        when(returnTypeRepository.findByUuid("type-uuid")).thenReturn(Optional.of(returnType));
        when(returnTypeRepository.save(any(ReturnType.class))).thenReturn(returnType);

        ReturnTypeResponse result = returnTypeService.update("type-uuid", request);

        assertThat(result.getName()).isEqualTo("Full Refund");
    }

    @Test
    void update_whenNotFound_shouldThrow() {
        ReturnTypeRequest request = new ReturnTypeRequest();
        request.setCode("REFUND");
        request.setName("Refund");

        when(returnTypeRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> returnTypeService.update("nonexistent", request))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void update_whenCodeConflicts_shouldThrow() {
        ReturnType other = ReturnType.builder()
                .id(2L).uuid("other-type")
                .code("EXCHANGE")
                .name("Exchange")
                .build();

        ReturnTypeRequest request = new ReturnTypeRequest();
        request.setCode("EXCHANGE");
        request.setName("Refund");

        when(returnTypeRepository.findByUuid("type-uuid")).thenReturn(Optional.of(returnType));
        when(returnTypeRepository.findByCode("EXCHANGE")).thenReturn(Optional.of(other));

        assertThatThrownBy(() -> returnTypeService.update("type-uuid", request))
                .isInstanceOf(BaseException.class);
    }

    // --- delete ---

    @Test
    void delete_shouldDeleteType() {
        when(returnTypeRepository.findByUuid("type-uuid")).thenReturn(Optional.of(returnType));

        returnTypeService.delete("type-uuid");

        verify(returnTypeRepository).delete(returnType);
    }

    @Test
    void delete_whenNotFound_shouldThrow() {
        when(returnTypeRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> returnTypeService.delete("nonexistent"))
                .isInstanceOf(BaseException.class);
    }
}
