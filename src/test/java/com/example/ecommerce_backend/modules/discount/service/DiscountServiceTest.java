package com.example.ecommerce_backend.modules.discount.service;

import com.example.ecommerce_backend.modules.discount.dto.request.DiscountRequest;
import com.example.ecommerce_backend.modules.discount.dto.response.DiscountResponse;
import com.example.ecommerce_backend.modules.discount.entity.Discount;
import com.example.ecommerce_backend.modules.discount.entity.DiscountAssignment;
import com.example.ecommerce_backend.modules.discount.entity.DiscountType;
import com.example.ecommerce_backend.modules.discount.exception.DiscountNotFoundException;
import com.example.ecommerce_backend.modules.discount.exception.DiscountTypeNotFoundException;
import com.example.ecommerce_backend.modules.discount.repository.DiscountAssignmentRepository;
import com.example.ecommerce_backend.modules.discount.repository.DiscountRepository;
import com.example.ecommerce_backend.modules.discount.repository.DiscountTypeRepository;
import com.example.ecommerce_backend.modules.user.entity.User;
import com.example.ecommerce_backend.modules.user.exception.UserNotFoundException;
import com.example.ecommerce_backend.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscountServiceTest {

    @Mock
    private DiscountRepository discountRepository;

    @Mock
    private DiscountTypeRepository discountTypeRepository;

    @Mock
    private DiscountAssignmentRepository discountAssignmentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DiscountService discountService;

    private DiscountType percentageType;
    private Discount globalDiscount;
    private Discount assignableDiscount;
    private Discount inactiveDiscount;
    private User user;
    private DiscountAssignment assignment;

    @BeforeEach
    void setUp() {
        percentageType = DiscountType.builder()
                .id(1L).uuid("dt-uuid-1").code("PERCENTAGE").name("Percentage")
                .computation("PERCENTAGE").isActive(true).build();

        user = User.builder()
                .id(1L).uuid("user-uuid-1").firstName("John").email("john@test.com").build();

        globalDiscount = Discount.builder()
                .id(1L).uuid("discount-uuid-1")
                .discountType(percentageType).discountValue(BigDecimal.TEN)
                .minOrderAmount(BigDecimal.valueOf(50)).maxDiscount(BigDecimal.valueOf(25))
                .isActive(true).isGlobal(true)
                .validFrom(Instant.now().minus(1, ChronoUnit.DAYS))
                .validUntil(Instant.now().plus(30, ChronoUnit.DAYS))
                .description("Global 10% off")
                .build();

        assignableDiscount = Discount.builder()
                .id(2L).uuid("discount-uuid-2")
                .discountType(percentageType).discountValue(BigDecimal.valueOf(15))
                .isActive(true).isGlobal(false)
                .validFrom(Instant.now().minus(1, ChronoUnit.DAYS))
                .validUntil(Instant.now().plus(30, ChronoUnit.DAYS))
                .description("User 15% off")
                .build();

        inactiveDiscount = Discount.builder()
                .id(3L).uuid("discount-uuid-3")
                .discountType(percentageType).discountValue(BigDecimal.valueOf(5))
                .isActive(false).isGlobal(false)
                .validFrom(Instant.now().minus(1, ChronoUnit.DAYS))
                .validUntil(Instant.now().plus(30, ChronoUnit.DAYS))
                .build();

        assignment = DiscountAssignment.builder()
                .id(1L).uuid("assign-uuid-1")
                .discount(assignableDiscount).user(user)
                .build();
    }

    // --- create ---

    @Test
    void create_shouldSaveAndReturnDiscount() {
        DiscountRequest request = new DiscountRequest();
        request.setDiscountTypeCode("PERCENTAGE");
        request.setDiscountValue(BigDecimal.TEN);
        request.setValidFrom(Instant.now());
        request.setValidUntil(Instant.now().plus(30, ChronoUnit.DAYS));

        when(discountTypeRepository.findByCode("PERCENTAGE")).thenReturn(Optional.of(percentageType));
        when(discountRepository.save(any(Discount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DiscountResponse result = discountService.create(request);

        assertThat(result.getDiscountValue()).isEqualByComparingTo(BigDecimal.TEN);
        assertThat(result.getDiscountType().getCode()).isEqualTo("PERCENTAGE");
        verify(discountRepository).save(any(Discount.class));
    }

    @Test
    void create_whenDiscountTypeNotFound_shouldThrow() {
        DiscountRequest request = new DiscountRequest();
        request.setDiscountTypeCode("INVALID");

        when(discountTypeRepository.findByCode("INVALID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> discountService.create(request))
                .isInstanceOf(DiscountTypeNotFoundException.class);
    }

    // --- createAssignable ---

    @Test
    void createAssignable_shouldCreateDiscountWithAssignments() {
        DiscountRequest request = new DiscountRequest();
        request.setDiscountTypeCode("PERCENTAGE");
        request.setDiscountValue(BigDecimal.TEN);
        request.setValidFrom(Instant.now());
        request.setValidUntil(Instant.now().plus(30, ChronoUnit.DAYS));

        when(discountTypeRepository.findByCode("PERCENTAGE")).thenReturn(Optional.of(percentageType));
        when(discountRepository.save(any(Discount.class))).thenAnswer(invocation -> {
            Discount d = invocation.getArgument(0);
            d.setUuid("new-discount-uuid");
            return d;
        });
        when(discountRepository.findByUuid("new-discount-uuid")).thenReturn(Optional.of(assignableDiscount));
        when(userRepository.findByUuid("user-uuid-1")).thenReturn(Optional.of(user));

        DiscountResponse result = discountService.createAssignable(request, List.of("user-uuid-1"));

        assertThat(result.getDiscountValue()).isEqualByComparingTo(BigDecimal.valueOf(15));
        verify(discountAssignmentRepository).save(any(DiscountAssignment.class));
    }

    // --- getByUuid ---

    @Test
    void getByUuid_shouldReturnDiscount() {
        when(discountRepository.findByUuid("discount-uuid-1")).thenReturn(Optional.of(globalDiscount));

        DiscountResponse result = discountService.getByUuid("discount-uuid-1");

        assertThat(result.getDescription()).isEqualTo("Global 10% off");
    }

    @Test
    void getByUuid_whenNotFound_shouldThrow() {
        when(discountRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> discountService.getByUuid("nonexistent"))
                .isInstanceOf(DiscountNotFoundException.class);
    }

    // --- getAll ---

    @Test
    void getAll_withNoFilters_shouldReturnAll() {
        when(discountRepository.findAll()).thenReturn(List.of(globalDiscount, assignableDiscount));

        List<DiscountResponse> result = discountService.getAll(null, null);

        assertThat(result).hasSize(2);
    }

    @Test
    void getAll_withActiveTrue_shouldReturnActive() {
        when(discountRepository.findByIsActiveTrueAndValidFromLessThanEqualAndValidUntilGreaterThanEqual(
                any(Instant.class), any(Instant.class)))
                .thenReturn(List.of(globalDiscount));

        List<DiscountResponse> result = discountService.getAll(true, null);

        assertThat(result).hasSize(1);
    }

    @Test
    void getAll_withActiveAndGlobal_shouldReturnFiltered() {
        when(discountRepository.findByIsGlobalAndIsActiveAndValidFromLessThanEqualAndValidUntilGreaterThanEqual(
                eq(true), eq(true), any(Instant.class), any(Instant.class)))
                .thenReturn(List.of(globalDiscount));

        List<DiscountResponse> result = discountService.getAll(true, true);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isGlobal()).isTrue();
    }

    @Test
    void getAll_withGlobalOnly_shouldReturnFiltered() {
        when(discountRepository.findAll()).thenReturn(List.of(globalDiscount, assignableDiscount));

        List<DiscountResponse> result = discountService.getAll(null, false);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isGlobal()).isFalse();
    }

    // --- update ---

    @Test
    void update_shouldUpdateAndReturnDiscount() {
        DiscountRequest request = new DiscountRequest();
        request.setDiscountTypeCode("PERCENTAGE");
        request.setDiscountValue(BigDecimal.valueOf(25));
        request.setValidFrom(Instant.now());
        request.setValidUntil(Instant.now().plus(30, ChronoUnit.DAYS));

        when(discountRepository.findByUuid("discount-uuid-1")).thenReturn(Optional.of(globalDiscount));
        when(discountTypeRepository.findByCode("PERCENTAGE")).thenReturn(Optional.of(percentageType));
        when(discountRepository.save(any(Discount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DiscountResponse result = discountService.update("discount-uuid-1", request);

        assertThat(result.getDiscountValue()).isEqualByComparingTo(BigDecimal.valueOf(25));
    }

    @Test
    void update_whenNotFound_shouldThrow() {
        when(discountRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> discountService.update("nonexistent", new DiscountRequest()))
                .isInstanceOf(DiscountNotFoundException.class);
    }

    // --- toggleStatus ---

    @Test
    void toggleStatus_shouldToggle() {
        when(discountRepository.findByUuid("discount-uuid-1")).thenReturn(Optional.of(globalDiscount));

        discountService.toggleStatus("discount-uuid-1", false);

        assertThat(globalDiscount.isActive()).isFalse();
        verify(discountRepository).save(globalDiscount);
    }

    @Test
    void toggleStatus_whenNotFound_shouldThrow() {
        when(discountRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> discountService.toggleStatus("nonexistent", true))
                .isInstanceOf(DiscountNotFoundException.class);
    }

    // --- delete ---

    @Test
    void delete_shouldDeleteDiscount() {
        when(discountRepository.findByUuid("discount-uuid-1")).thenReturn(Optional.of(globalDiscount));

        discountService.delete("discount-uuid-1");

        verify(discountRepository).delete(globalDiscount);
    }

    @Test
    void delete_whenNotFound_shouldThrow() {
        when(discountRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> discountService.delete("nonexistent"))
                .isInstanceOf(DiscountNotFoundException.class);
    }

    // --- getEligibleDiscounts ---

    @Test
    void getEligibleDiscounts_shouldReturnGlobalAndAssigned() {
        when(discountRepository.findByIsGlobalAndIsActiveAndValidFromLessThanEqualAndValidUntilGreaterThanEqual(
                eq(true), eq(true), any(Instant.class), any(Instant.class)))
                .thenReturn(List.of(globalDiscount));
        when(discountAssignmentRepository.findByUserIdAndDiscountIsActiveTrueAndDiscountValidFromLessThanEqualAndDiscountValidUntilGreaterThanEqual(
                eq(1L), any(Instant.class), any(Instant.class)))
                .thenReturn(List.of(assignment));

        List<DiscountResponse> result = discountService.getEligibleDiscounts(1L);

        assertThat(result).hasSize(2);
    }

    @Test
    void getEligibleDiscounts_withNoEligible_shouldReturnEmpty() {
        when(discountRepository.findByIsGlobalAndIsActiveAndValidFromLessThanEqualAndValidUntilGreaterThanEqual(
                eq(true), eq(true), any(Instant.class), any(Instant.class)))
                .thenReturn(List.of());
        when(discountAssignmentRepository.findByUserIdAndDiscountIsActiveTrueAndDiscountValidFromLessThanEqualAndDiscountValidUntilGreaterThanEqual(
                eq(1L), any(Instant.class), any(Instant.class)))
                .thenReturn(List.of());

        List<DiscountResponse> result = discountService.getEligibleDiscounts(1L);

        assertThat(result).isEmpty();
    }

    // --- assignToUsers ---

    @Test
    void assignToUsers_shouldCreateAssignments() {
        when(discountRepository.findByUuid("discount-uuid-1")).thenReturn(Optional.of(globalDiscount));
        when(userRepository.findByUuid("user-uuid-1")).thenReturn(Optional.of(user));
        when(discountAssignmentRepository.findByDiscountIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        discountService.assignToUsers("discount-uuid-1", List.of("user-uuid-1"));

        verify(discountAssignmentRepository).save(any(DiscountAssignment.class));
    }

    @Test
    void assignToUsers_whenAlreadyAssigned_shouldSkip() {
        when(discountRepository.findByUuid("discount-uuid-1")).thenReturn(Optional.of(globalDiscount));
        when(userRepository.findByUuid("user-uuid-1")).thenReturn(Optional.of(user));
        when(discountAssignmentRepository.findByDiscountIdAndUserId(1L, 1L)).thenReturn(Optional.of(assignment));

        discountService.assignToUsers("discount-uuid-1", List.of("user-uuid-1"));

        verify(discountAssignmentRepository, never()).save(any());
    }

    @Test
    void assignToUsers_whenUserNotFound_shouldThrow() {
        when(discountRepository.findByUuid("discount-uuid-1")).thenReturn(Optional.of(globalDiscount));
        when(userRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> discountService.assignToUsers("discount-uuid-1", List.of("nonexistent")))
                .isInstanceOf(UserNotFoundException.class);
    }

    // --- removeAssignment ---

    @Test
    void removeAssignment_shouldDeleteAssignment() {
        when(discountRepository.findByUuid("discount-uuid-2")).thenReturn(Optional.of(assignableDiscount));
        when(userRepository.findByUuid("user-uuid-1")).thenReturn(Optional.of(user));
        when(discountAssignmentRepository.findByDiscountIdAndUserId(2L, 1L)).thenReturn(Optional.of(assignment));

        discountService.removeAssignment("discount-uuid-2", "user-uuid-1");

        verify(discountAssignmentRepository).delete(assignment);
    }

    @Test
    void removeAssignment_whenAssignmentNotFound_shouldThrow() {
        when(discountRepository.findByUuid("discount-uuid-2")).thenReturn(Optional.of(assignableDiscount));
        when(userRepository.findByUuid("user-uuid-1")).thenReturn(Optional.of(user));
        when(discountAssignmentRepository.findByDiscountIdAndUserId(2L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> discountService.removeAssignment("discount-uuid-2", "user-uuid-1"))
                .isInstanceOf(DiscountNotFoundException.class);
    }
}
