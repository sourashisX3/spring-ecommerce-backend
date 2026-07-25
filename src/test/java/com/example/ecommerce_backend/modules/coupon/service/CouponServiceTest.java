package com.example.ecommerce_backend.modules.coupon.service;

import com.example.ecommerce_backend.modules.coupon.dto.request.CouponRequest;
import com.example.ecommerce_backend.modules.coupon.dto.response.CouponResponse;
import com.example.ecommerce_backend.modules.coupon.entity.Coupon;
import com.example.ecommerce_backend.modules.coupon.entity.CouponAssignment;
import com.example.ecommerce_backend.modules.coupon.entity.CouponUsage;
import com.example.ecommerce_backend.modules.coupon.exception.CouponExhaustedException;
import com.example.ecommerce_backend.modules.coupon.exception.CouponExpiredException;
import com.example.ecommerce_backend.modules.coupon.exception.CouponNotFoundException;
import com.example.ecommerce_backend.modules.coupon.repository.CouponAssignmentRepository;
import com.example.ecommerce_backend.modules.coupon.repository.CouponRepository;
import com.example.ecommerce_backend.modules.coupon.repository.CouponUsageRepository;
import com.example.ecommerce_backend.modules.discount.entity.DiscountType;
import com.example.ecommerce_backend.modules.discount.exception.DiscountTypeNotFoundException;
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
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private CouponAssignmentRepository couponAssignmentRepository;

    @Mock
    private CouponUsageRepository couponUsageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DiscountTypeRepository discountTypeRepository;

    @InjectMocks
    private CouponService couponService;

    private DiscountType percentageType;
    private DiscountType fixedType;
    private Coupon globalCoupon;
    private Coupon assignableCoupon;
    private Coupon expiredCoupon;
    private Coupon exhaustedCoupon;
    private User user;
    private CouponAssignment assignment;

    @BeforeEach
    void setUp() {
        percentageType = DiscountType.builder()
                .id(1L).uuid("dt-uuid-1").code("PERCENTAGE").name("Percentage")
                .computation("PERCENTAGE").isActive(true).build();

        fixedType = DiscountType.builder()
                .id(2L).uuid("dt-uuid-2").code("FIXED").name("Fixed Amount")
                .computation("FIXED").isActive(true).build();

        user = User.builder()
                .id(1L).uuid("user-uuid-1").firstName("John").email("john@test.com").build();

        globalCoupon = Coupon.builder()
                .id(1L).uuid("coupon-uuid-1").code("SAVE10")
                .discountType(percentageType).discountValue(BigDecimal.TEN)
                .minOrderAmount(BigDecimal.valueOf(50)).maxDiscount(BigDecimal.valueOf(25))
                .usageLimit(100).usageLimitPerUser(5)
                .isActive(true).isGlobal(true).totalUsed(0)
                .validFrom(Instant.now().minus(1, ChronoUnit.DAYS))
                .validUntil(Instant.now().plus(30, ChronoUnit.DAYS))
                .build();

        assignableCoupon = Coupon.builder()
                .id(2L).uuid("coupon-uuid-2").code("USER10")
                .discountType(fixedType).discountValue(BigDecimal.valueOf(15))
                .isActive(true).isGlobal(false).totalUsed(0)
                .validFrom(Instant.now().minus(1, ChronoUnit.DAYS))
                .validUntil(Instant.now().plus(30, ChronoUnit.DAYS))
                .build();

        expiredCoupon = Coupon.builder()
                .id(3L).uuid("coupon-uuid-3").code("EXPIRED")
                .discountType(percentageType).discountValue(BigDecimal.valueOf(5))
                .isActive(true).isGlobal(true).totalUsed(0)
                .validFrom(Instant.now().minus(60, ChronoUnit.DAYS))
                .validUntil(Instant.now().minus(1, ChronoUnit.DAYS))
                .build();

        exhaustedCoupon = Coupon.builder()
                .id(4L).uuid("coupon-uuid-4").code("EXHAUSTED")
                .discountType(fixedType).discountValue(BigDecimal.valueOf(10))
                .isActive(true).isGlobal(true).totalUsed(5)
                .usageLimit(5)
                .validFrom(Instant.now().minus(1, ChronoUnit.DAYS))
                .validUntil(Instant.now().plus(30, ChronoUnit.DAYS))
                .build();

        assignment = CouponAssignment.builder()
                .id(1L).uuid("assign-uuid-1")
                .coupon(assignableCoupon).user(user)
                .usedCount(0).build();
    }

    // --- create ---

    @Test
    void create_shouldSaveAndReturnCoupon() {
        CouponRequest request = new CouponRequest();
        request.setCode("NEW10");
        request.setDiscountTypeCode("PERCENTAGE");
        request.setDiscountValue(BigDecimal.TEN);
        request.setValidFrom(Instant.now());
        request.setValidUntil(Instant.now().plus(30, ChronoUnit.DAYS));

        when(discountTypeRepository.findByCode("PERCENTAGE")).thenReturn(Optional.of(percentageType));
        when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CouponResponse result = couponService.create(request);

        assertThat(result.getCode()).isEqualTo("NEW10");
        assertThat(result.getDiscountType().getCode()).isEqualTo("PERCENTAGE");
        verify(couponRepository).save(any(Coupon.class));
    }

    @Test
    void create_whenDiscountTypeNotFound_shouldThrow() {
        CouponRequest request = new CouponRequest();
        request.setDiscountTypeCode("INVALID");

        when(discountTypeRepository.findByCode("INVALID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> couponService.create(request))
                .isInstanceOf(DiscountTypeNotFoundException.class);
    }

    // --- createAssignable ---

    @Test
    void createAssignable_shouldCreateCouponWithAssignments() {
        CouponRequest request = new CouponRequest();
        request.setCode("ASSIGN10");
        request.setDiscountTypeCode("PERCENTAGE");
        request.setDiscountValue(BigDecimal.TEN);
        request.setValidFrom(Instant.now());
        request.setValidUntil(Instant.now().plus(30, ChronoUnit.DAYS));

        when(discountTypeRepository.findByCode("PERCENTAGE")).thenReturn(Optional.of(percentageType));
        when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> {
            Coupon c = invocation.getArgument(0);
            c.setUuid("new-assign-uuid");
            return c;
        });
        when(userRepository.findByUuid("user-uuid-1")).thenReturn(Optional.of(user));

        CouponResponse result = couponService.createAssignable(request, List.of("user-uuid-1"));

        assertThat(result.getCode()).isEqualTo("ASSIGN10");
        verify(couponAssignmentRepository).save(any(CouponAssignment.class));
    }

    @Test
    void createAssignable_whenUserNotFound_shouldThrow() {
        CouponRequest request = new CouponRequest();
        request.setCode("ASSIGN10");
        request.setDiscountTypeCode("PERCENTAGE");
        request.setDiscountValue(BigDecimal.TEN);
        request.setValidFrom(Instant.now());
        request.setValidUntil(Instant.now().plus(30, ChronoUnit.DAYS));

        when(discountTypeRepository.findByCode("PERCENTAGE")).thenReturn(Optional.of(percentageType));
        when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> {
            Coupon c = invocation.getArgument(0);
            c.setUuid("new-assign-uuid");
            return c;
        });
        when(userRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> couponService.createAssignable(request, List.of("nonexistent")))
                .isInstanceOf(UserNotFoundException.class);
    }

    // --- getByUuid ---

    @Test
    void getByUuid_shouldReturnCoupon() {
        when(couponRepository.findByUuid("coupon-uuid-1")).thenReturn(Optional.of(globalCoupon));

        CouponResponse result = couponService.getByUuid("coupon-uuid-1");

        assertThat(result.getCode()).isEqualTo("SAVE10");
    }

    @Test
    void getByUuid_whenNotFound_shouldThrow() {
        when(couponRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> couponService.getByUuid("nonexistent"))
                .isInstanceOf(CouponNotFoundException.class);
    }

    // --- getAll ---

    @Test
    void getAll_withNoFilters_shouldReturnAll() {
        when(couponRepository.findAll()).thenReturn(List.of(globalCoupon, assignableCoupon));

        List<CouponResponse> result = couponService.getAll(null, null);

        assertThat(result).hasSize(2);
    }

    @Test
    void getAll_withActiveTrue_shouldReturnActive() {
        when(couponRepository.findByIsActiveTrueAndValidFromLessThanEqualAndValidUntilGreaterThanEqual(
                any(Instant.class), any(Instant.class)))
                .thenReturn(List.of(globalCoupon));

        List<CouponResponse> result = couponService.getAll(true, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("SAVE10");
    }

    @Test
    void getAll_withActiveFalse_shouldReturnInactive() {
        Coupon inactiveCoupon = Coupon.builder()
                .id(5L).uuid("coupon-uuid-5").code("INACTIVE")
                .discountType(percentageType).discountValue(BigDecimal.TEN)
                .isActive(false).isGlobal(true)
                .validFrom(Instant.now().minus(1, ChronoUnit.DAYS))
                .validUntil(Instant.now().plus(30, ChronoUnit.DAYS))
                .build();

        when(couponRepository.findByIsActiveTrueAndValidFromLessThanEqualAndValidUntilGreaterThanEqual(
                any(Instant.class), any(Instant.class)))
                .thenReturn(List.of(globalCoupon));
        when(couponRepository.findAll()).thenReturn(List.of(globalCoupon, inactiveCoupon));

        List<CouponResponse> result = couponService.getAll(false, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("INACTIVE");
    }

    @Test
    void getAll_withGlobalFilter_shouldReturnFiltered() {
        when(couponRepository.findAll()).thenReturn(List.of(globalCoupon, assignableCoupon));

        List<CouponResponse> result = couponService.getAll(null, true);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isGlobal()).isTrue();
    }

    // --- update ---

    @Test
    void update_shouldUpdateAndReturnCoupon() {
        CouponRequest request = new CouponRequest();
        request.setCode("UPDATED10");
        request.setDiscountTypeCode("FIXED");
        request.setDiscountValue(BigDecimal.valueOf(20));
        request.setValidFrom(Instant.now());
        request.setValidUntil(Instant.now().plus(30, ChronoUnit.DAYS));

        when(couponRepository.findByUuid("coupon-uuid-1")).thenReturn(Optional.of(globalCoupon));
        when(discountTypeRepository.findByCode("FIXED")).thenReturn(Optional.of(fixedType));
        when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CouponResponse result = couponService.update("coupon-uuid-1", request);

        assertThat(result.getCode()).isEqualTo("UPDATED10");
        assertThat(result.getDiscountValue()).isEqualByComparingTo(BigDecimal.valueOf(20));
    }

    @Test
    void update_whenCouponNotFound_shouldThrow() {
        when(couponRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> couponService.update("nonexistent", new CouponRequest()))
                .isInstanceOf(CouponNotFoundException.class);
    }

    // --- toggleStatus ---

    @Test
    void toggleStatus_shouldToggle() {
        when(couponRepository.findByUuid("coupon-uuid-1")).thenReturn(Optional.of(globalCoupon));

        couponService.toggleStatus("coupon-uuid-1", false);

        assertThat(globalCoupon.isActive()).isFalse();
        verify(couponRepository).save(globalCoupon);
    }

    @Test
    void toggleStatus_whenNotFound_shouldThrow() {
        when(couponRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> couponService.toggleStatus("nonexistent", true))
                .isInstanceOf(CouponNotFoundException.class);
    }

    // --- delete ---

    @Test
    void delete_shouldDeleteCoupon() {
        when(couponRepository.findByUuid("coupon-uuid-1")).thenReturn(Optional.of(globalCoupon));

        couponService.delete("coupon-uuid-1");

        verify(couponRepository).delete(globalCoupon);
    }

    @Test
    void delete_whenNotFound_shouldThrow() {
        when(couponRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> couponService.delete("nonexistent"))
                .isInstanceOf(CouponNotFoundException.class);
    }

    // --- assignToUsers ---

    @Test
    void assignToUsers_shouldCreateAssignments() {
        when(couponRepository.findByUuid("coupon-uuid-1")).thenReturn(Optional.of(globalCoupon));
        when(userRepository.findByUuid("user-uuid-1")).thenReturn(Optional.of(user));
        when(couponAssignmentRepository.findByCouponIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        couponService.assignToUsers("coupon-uuid-1", List.of("user-uuid-1"));

        verify(couponAssignmentRepository).save(any(CouponAssignment.class));
    }

    @Test
    void assignToUsers_whenAlreadyAssigned_shouldSkip() {
        when(couponRepository.findByUuid("coupon-uuid-1")).thenReturn(Optional.of(globalCoupon));
        when(userRepository.findByUuid("user-uuid-1")).thenReturn(Optional.of(user));
        when(couponAssignmentRepository.findByCouponIdAndUserId(1L, 1L)).thenReturn(Optional.of(assignment));

        couponService.assignToUsers("coupon-uuid-1", List.of("user-uuid-1"));

        verify(couponAssignmentRepository, never()).save(any());
    }

    @Test
    void assignToUsers_whenCouponNotFound_shouldThrow() {
        when(couponRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> couponService.assignToUsers("nonexistent", List.of("user-uuid-1")))
                .isInstanceOf(CouponNotFoundException.class);
    }

    // --- removeAssignment ---

    @Test
    void removeAssignment_shouldDeleteAssignment() {
        when(couponRepository.findByUuid("coupon-uuid-2")).thenReturn(Optional.of(assignableCoupon));
        when(userRepository.findByUuid("user-uuid-1")).thenReturn(Optional.of(user));
        when(couponAssignmentRepository.findByCouponIdAndUserId(2L, 1L)).thenReturn(Optional.of(assignment));

        couponService.removeAssignment("coupon-uuid-2", "user-uuid-1");

        verify(couponAssignmentRepository).delete(assignment);
    }

    @Test
    void removeAssignment_whenAssignmentNotFound_shouldThrow() {
        when(couponRepository.findByUuid("coupon-uuid-2")).thenReturn(Optional.of(assignableCoupon));
        when(userRepository.findByUuid("user-uuid-1")).thenReturn(Optional.of(user));
        when(couponAssignmentRepository.findByCouponIdAndUserId(2L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> couponService.removeAssignment("coupon-uuid-2", "user-uuid-1"))
                .isInstanceOf(CouponNotFoundException.class);
    }

    // --- validateAndApply ---

    @Test
    void validateAndApply_withPercentageCoupon_shouldCalculateDiscount() {
        when(couponRepository.findByCodeWithLock("SAVE10")).thenReturn(Optional.of(globalCoupon));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BigDecimal discount = couponService.validateAndApply("SAVE10", 1L, BigDecimal.valueOf(100), null);

        assertThat(discount).isEqualByComparingTo(BigDecimal.TEN);
        verify(couponUsageRepository).save(any(CouponUsage.class));
    }

    @Test
    void validateAndApply_withPercentageAndMaxDiscount_shouldCapDiscount() {
        when(couponRepository.findByCodeWithLock("SAVE10")).thenReturn(Optional.of(globalCoupon));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BigDecimal discount = couponService.validateAndApply("SAVE10", 1L, BigDecimal.valueOf(1000), null);

        assertThat(discount).isEqualByComparingTo(BigDecimal.valueOf(25));
    }

    @Test
    void validateAndApply_withFixedCoupon_shouldReturnFixedValue() {
        when(couponRepository.findByCodeWithLock("USER10")).thenReturn(Optional.of(assignableCoupon));
        when(couponAssignmentRepository.findByCouponIdAndUserId(2L, 1L)).thenReturn(Optional.of(assignment));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BigDecimal discount = couponService.validateAndApply("USER10", 1L, BigDecimal.valueOf(100), null);

        assertThat(discount).isEqualByComparingTo(BigDecimal.valueOf(15));
    }

    @Test
    void validateAndApply_withFixedCouponExceedingSubtotal_shouldCapToSubtotal() {
        when(couponRepository.findByCodeWithLock("USER10")).thenReturn(Optional.of(assignableCoupon));
        when(couponAssignmentRepository.findByCouponIdAndUserId(2L, 1L)).thenReturn(Optional.of(assignment));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BigDecimal discount = couponService.validateAndApply("USER10", 1L, BigDecimal.valueOf(5), null);

        assertThat(discount).isEqualByComparingTo(BigDecimal.valueOf(5));
    }

    @Test
    void validateAndApply_whenExpired_shouldThrow() {
        when(couponRepository.findByCodeWithLock("EXPIRED")).thenReturn(Optional.of(expiredCoupon));

        assertThatThrownBy(() -> couponService.validateAndApply("EXPIRED", 1L, BigDecimal.valueOf(100), null))
                .isInstanceOf(CouponExpiredException.class);
    }

    @Test
    void validateAndApply_whenExhausted_shouldThrow() {
        when(couponRepository.findByCodeWithLock("EXHAUSTED")).thenReturn(Optional.of(exhaustedCoupon));

        assertThatThrownBy(() -> couponService.validateAndApply("EXHAUSTED", 1L, BigDecimal.valueOf(100), null))
                .isInstanceOf(CouponExhaustedException.class);
    }

    @Test
    void validateAndApply_whenExhaustedPerUser_shouldThrow() {
        when(couponRepository.findByCodeWithLock("SAVE10")).thenReturn(Optional.of(globalCoupon));
        when(couponUsageRepository.countByUserIdAndCouponId(1L, 1L)).thenReturn(5L);

        assertThatThrownBy(() -> couponService.validateAndApply("SAVE10", 1L, BigDecimal.valueOf(100), null))
                .isInstanceOf(CouponExhaustedException.class);
    }

    @Test
    void validateAndApply_whenMinOrderNotMet_shouldThrow() {
        when(couponRepository.findByCodeWithLock("SAVE10")).thenReturn(Optional.of(globalCoupon));

        assertThatThrownBy(() -> couponService.validateAndApply("SAVE10", 1L, BigDecimal.valueOf(30), null))
                .isInstanceOf(CouponExpiredException.class);
    }

    @Test
    void validateAndApply_whenCouponNotGlobalAndNotAssigned_shouldThrow() {
        when(couponRepository.findByCodeWithLock("USER10")).thenReturn(Optional.of(assignableCoupon));
        when(couponAssignmentRepository.findByCouponIdAndUserId(2L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> couponService.validateAndApply("USER10", 1L, BigDecimal.valueOf(100), null))
                .isInstanceOf(CouponNotFoundException.class);
    }

    @Test
    void validateAndApply_whenCouponNotFound_shouldThrow() {
        when(couponRepository.findByCodeWithLock("NONEXISTENT")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> couponService.validateAndApply("NONEXISTENT", 1L, BigDecimal.valueOf(100), null))
                .isInstanceOf(CouponNotFoundException.class);
    }
}
