package com.example.ecommerce_backend.modules.offer.service;

import com.example.ecommerce_backend.modules.discount.entity.DiscountType;
import com.example.ecommerce_backend.modules.discount.exception.DiscountTypeNotFoundException;
import com.example.ecommerce_backend.modules.discount.repository.DiscountTypeRepository;
import com.example.ecommerce_backend.modules.offer.dto.request.OfferRequest;
import com.example.ecommerce_backend.modules.offer.dto.response.OfferResponse;
import com.example.ecommerce_backend.modules.offer.entity.Offer;
import com.example.ecommerce_backend.modules.offer.entity.OfferAssignment;
import com.example.ecommerce_backend.modules.offer.exception.OfferNotFoundException;
import com.example.ecommerce_backend.modules.offer.mapper.OfferMapper;
import com.example.ecommerce_backend.modules.offer.repository.OfferAssignmentRepository;
import com.example.ecommerce_backend.modules.offer.repository.OfferRepository;
import com.example.ecommerce_backend.modules.offer.repository.OfferUsageRepository;
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
class OfferServiceTest {

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private OfferAssignmentRepository offerAssignmentRepository;

    @Mock
    private OfferUsageRepository offerUsageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DiscountTypeRepository discountTypeRepository;

    @InjectMocks
    private OfferService offerService;

    private DiscountType percentageType;
    private Offer globalOffer;
    private Offer assignableOffer;
    private Offer inactiveOffer;
    private User user;
    private OfferAssignment assignment;

    @BeforeEach
    void setUp() {
        percentageType = DiscountType.builder()
                .id(1L).uuid("dt-uuid-1").code("PERCENTAGE").name("Percentage")
                .computation("PERCENTAGE").isActive(true).build();

        user = User.builder()
                .id(1L).uuid("user-uuid-1").firstName("John").email("john@test.com").build();

        globalOffer = Offer.builder()
                .id(1L).uuid("offer-uuid-1").title("Summer Sale")
                .description("Summer sale offer")
                .discountType(percentageType).discountValue(BigDecimal.TEN)
                .minOrderAmount(BigDecimal.valueOf(50)).maxDiscount(BigDecimal.valueOf(25))
                .usageLimit(100).usageLimitPerUser(5)
                .isActive(true).isGlobal(true).totalUsed(0)
                .validFrom(Instant.now().minus(1, ChronoUnit.DAYS))
                .validUntil(Instant.now().plus(30, ChronoUnit.DAYS))
                .applicableTo("all").applicableIds(null)
                .build();

        assignableOffer = Offer.builder()
                .id(2L).uuid("offer-uuid-2").title("User Offer")
                .discountType(percentageType).discountValue(BigDecimal.valueOf(15))
                .isActive(true).isGlobal(false).totalUsed(0)
                .validFrom(Instant.now().minus(1, ChronoUnit.DAYS))
                .validUntil(Instant.now().plus(30, ChronoUnit.DAYS))
                .applicableTo("all").applicableIds(null)
                .build();

        inactiveOffer = Offer.builder()
                .id(3L).uuid("offer-uuid-3").title("Inactive Offer")
                .discountType(percentageType).discountValue(BigDecimal.valueOf(5))
                .isActive(false).isGlobal(true)
                .validFrom(Instant.now().minus(1, ChronoUnit.DAYS))
                .validUntil(Instant.now().plus(30, ChronoUnit.DAYS))
                .applicableTo("all").applicableIds(null)
                .build();

        assignment = OfferAssignment.builder()
                .id(1L).uuid("assign-uuid-1")
                .offer(assignableOffer).user(user)
                .usedCount(0).build();
    }

    // --- create ---

    @Test
    void create_shouldSaveAndReturnOffer() {
        OfferRequest request = new OfferRequest();
        request.setTitle("New Offer");
        request.setDiscountTypeCode("PERCENTAGE");
        request.setDiscountValue(BigDecimal.TEN);
        request.setValidFrom(Instant.now());
        request.setValidUntil(Instant.now().plus(30, ChronoUnit.DAYS));
        request.setApplicableTo("all");

        when(discountTypeRepository.findByCode("PERCENTAGE")).thenReturn(Optional.of(percentageType));
        when(offerRepository.save(any(Offer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OfferResponse result = offerService.create(request);

        assertThat(result.getTitle()).isEqualTo("New Offer");
        assertThat(result.getDiscountType().getCode()).isEqualTo("PERCENTAGE");
        verify(offerRepository).save(any(Offer.class));
    }

    @Test
    void create_whenDiscountTypeNotFound_shouldThrow() {
        OfferRequest request = new OfferRequest();
        request.setDiscountTypeCode("INVALID");

        when(discountTypeRepository.findByCode("INVALID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> offerService.create(request))
                .isInstanceOf(DiscountTypeNotFoundException.class);
    }

    // --- createAssignable ---

    @Test
    void createAssignable_shouldCreateOfferWithAssignments() {
        OfferRequest request = new OfferRequest();
        request.setTitle("Assignable Offer");
        request.setDiscountTypeCode("PERCENTAGE");
        request.setDiscountValue(BigDecimal.TEN);
        request.setValidFrom(Instant.now());
        request.setValidUntil(Instant.now().plus(30, ChronoUnit.DAYS));
        request.setApplicableTo("all");

        when(discountTypeRepository.findByCode("PERCENTAGE")).thenReturn(Optional.of(percentageType));
        when(offerRepository.save(any(Offer.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.findByUuid("user-uuid-1")).thenReturn(Optional.of(user));

        OfferResponse result = offerService.createAssignable(request, List.of("user-uuid-1"));

        assertThat(result.getTitle()).isEqualTo("Assignable Offer");
        assertThat(result.getAssignedUserUuids()).contains("user-uuid-1");
        verify(offerAssignmentRepository).save(any(OfferAssignment.class));
    }

    @Test
    void createAssignable_whenUserNotFound_shouldThrow() {
        OfferRequest request = new OfferRequest();
        request.setTitle("Assignable Offer");
        request.setDiscountTypeCode("PERCENTAGE");
        request.setDiscountValue(BigDecimal.TEN);
        request.setValidFrom(Instant.now());
        request.setValidUntil(Instant.now().plus(30, ChronoUnit.DAYS));

        when(discountTypeRepository.findByCode("PERCENTAGE")).thenReturn(Optional.of(percentageType));
        when(offerRepository.save(any(Offer.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> offerService.createAssignable(request, List.of("nonexistent")))
                .isInstanceOf(UserNotFoundException.class);
    }

    // --- getByUuid ---

    @Test
    void getByUuid_shouldReturnOffer() {
        when(offerRepository.findByUuid("offer-uuid-1")).thenReturn(Optional.of(globalOffer));

        OfferResponse result = offerService.getByUuid("offer-uuid-1");

        assertThat(result.getTitle()).isEqualTo("Summer Sale");
    }

    @Test
    void getByUuid_whenNotFound_shouldThrow() {
        when(offerRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> offerService.getByUuid("nonexistent"))
                .isInstanceOf(OfferNotFoundException.class);
    }

    // --- getAll ---

    @Test
    void getAll_withNoFilters_shouldReturnAll() {
        when(offerRepository.findAll()).thenReturn(List.of(globalOffer, assignableOffer));

        List<OfferResponse> result = offerService.getAll(null, null);

        assertThat(result).hasSize(2);
    }

    @Test
    void getAll_withActiveTrue_shouldReturnActive() {
        when(offerRepository.findByIsActiveAndValidFromLessThanEqualAndValidUntilGreaterThanEqual(
                eq(true), any(Instant.class), any(Instant.class)))
                .thenReturn(List.of(globalOffer));

        List<OfferResponse> result = offerService.getAll(true, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Summer Sale");
    }

    @Test
    void getAll_withActiveFalse_shouldReturnInactive() {
        when(offerRepository.findByIsActiveAndValidFromLessThanEqualAndValidUntilGreaterThanEqual(
                eq(false), any(Instant.class), any(Instant.class)))
                .thenReturn(List.of(globalOffer));

        List<OfferResponse> result = offerService.getAll(false, null);

        assertThat(result).hasSize(1);
    }

    @Test
    void getAll_withActiveAndGlobal_shouldReturnFiltered() {
        when(offerRepository.findByIsGlobalAndIsActiveAndValidFromLessThanEqualAndValidUntilGreaterThanEqual(
                eq(true), eq(true), any(Instant.class), any(Instant.class)))
                .thenReturn(List.of(globalOffer));

        List<OfferResponse> result = offerService.getAll(true, true);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isGlobal()).isTrue();
    }

    @Test
    void getAll_withGlobalOnly_shouldReturnFiltered() {
        when(offerRepository.findAll()).thenReturn(List.of(globalOffer, assignableOffer));

        List<OfferResponse> result = offerService.getAll(null, false);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isGlobal()).isFalse();
    }

    // --- update ---

    @Test
    void update_shouldUpdateAndReturnOffer() {
        OfferRequest request = new OfferRequest();
        request.setTitle("Updated Offer");
        request.setDiscountTypeCode("PERCENTAGE");
        request.setDiscountValue(BigDecimal.valueOf(25));
        request.setValidFrom(Instant.now());
        request.setValidUntil(Instant.now().plus(30, ChronoUnit.DAYS));
        request.setApplicableTo("all");

        when(offerRepository.findByUuid("offer-uuid-1")).thenReturn(Optional.of(globalOffer));
        when(discountTypeRepository.findByCode("PERCENTAGE")).thenReturn(Optional.of(percentageType));
        when(offerRepository.save(any(Offer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OfferResponse result = offerService.update("offer-uuid-1", request);

        assertThat(result.getTitle()).isEqualTo("Updated Offer");
    }

    @Test
    void update_whenNotFound_shouldThrow() {
        when(offerRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> offerService.update("nonexistent", new OfferRequest()))
                .isInstanceOf(OfferNotFoundException.class);
    }

    // --- toggleStatus ---

    @Test
    void toggleStatus_shouldToggle() {
        when(offerRepository.findByUuid("offer-uuid-1")).thenReturn(Optional.of(globalOffer));

        offerService.toggleStatus("offer-uuid-1", false);

        assertThat(globalOffer.isActive()).isFalse();
        verify(offerRepository).save(globalOffer);
    }

    @Test
    void toggleStatus_whenNotFound_shouldThrow() {
        when(offerRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> offerService.toggleStatus("nonexistent", true))
                .isInstanceOf(OfferNotFoundException.class);
    }

    // --- delete ---

    @Test
    void delete_shouldDeleteOffer() {
        when(offerRepository.findByUuid("offer-uuid-1")).thenReturn(Optional.of(globalOffer));

        offerService.delete("offer-uuid-1");

        verify(offerRepository).delete(globalOffer);
    }

    @Test
    void delete_whenNotFound_shouldThrow() {
        when(offerRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> offerService.delete("nonexistent"))
                .isInstanceOf(OfferNotFoundException.class);
    }

    // --- getEligibleOffers ---

    @Test
    void getEligibleOffers_shouldReturnGlobalAndAssigned() {
        when(offerRepository.findByIsGlobalAndIsActiveAndValidFromLessThanEqualAndValidUntilGreaterThanEqual(
                eq(true), eq(true), any(Instant.class), any(Instant.class)))
                .thenReturn(List.of(globalOffer));
        when(offerAssignmentRepository.findByUserIdAndOfferIsActiveTrueAndOfferValidFromLessThanEqualAndOfferValidUntilGreaterThanEqual(
                eq(1L), any(Instant.class), any(Instant.class)))
                .thenReturn(List.of(assignment));

        List<OfferResponse> result = offerService.getEligibleOffers(1L);

        assertThat(result).hasSize(2);
    }

    @Test
    void getEligibleOffers_withNoEligible_shouldReturnEmpty() {
        when(offerRepository.findByIsGlobalAndIsActiveAndValidFromLessThanEqualAndValidUntilGreaterThanEqual(
                eq(true), eq(true), any(Instant.class), any(Instant.class)))
                .thenReturn(List.of());
        when(offerAssignmentRepository.findByUserIdAndOfferIsActiveTrueAndOfferValidFromLessThanEqualAndOfferValidUntilGreaterThanEqual(
                eq(1L), any(Instant.class), any(Instant.class)))
                .thenReturn(List.of());

        List<OfferResponse> result = offerService.getEligibleOffers(1L);

        assertThat(result).isEmpty();
    }

    // --- assignToUsers ---

    @Test
    void assignToUsers_shouldCreateAssignments() {
        when(offerRepository.findByUuid("offer-uuid-1")).thenReturn(Optional.of(globalOffer));
        when(userRepository.findByUuid("user-uuid-1")).thenReturn(Optional.of(user));
        when(offerAssignmentRepository.findByOfferIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        offerService.assignToUsers("offer-uuid-1", List.of("user-uuid-1"), 5);

        verify(offerAssignmentRepository).save(any(OfferAssignment.class));
    }

    @Test
    void assignToUsers_whenAlreadyAssigned_shouldSkip() {
        when(offerRepository.findByUuid("offer-uuid-1")).thenReturn(Optional.of(globalOffer));
        when(userRepository.findByUuid("user-uuid-1")).thenReturn(Optional.of(user));
        when(offerAssignmentRepository.findByOfferIdAndUserId(1L, 1L)).thenReturn(Optional.of(assignment));

        offerService.assignToUsers("offer-uuid-1", List.of("user-uuid-1"), 5);

        verify(offerAssignmentRepository, never()).save(any());
    }

    @Test
    void assignToUsers_whenUserNotFound_shouldThrow() {
        when(offerRepository.findByUuid("offer-uuid-1")).thenReturn(Optional.of(globalOffer));
        when(userRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> offerService.assignToUsers("offer-uuid-1", List.of("nonexistent"), 5))
                .isInstanceOf(UserNotFoundException.class);
    }

    // --- removeAssignment ---

    @Test
    void removeAssignment_shouldDeleteAssignment() {
        when(offerRepository.findByUuid("offer-uuid-2")).thenReturn(Optional.of(assignableOffer));
        when(userRepository.findByUuid("user-uuid-1")).thenReturn(Optional.of(user));
        when(offerAssignmentRepository.findByOfferIdAndUserId(2L, 1L)).thenReturn(Optional.of(assignment));

        offerService.removeAssignment("offer-uuid-2", "user-uuid-1");

        verify(offerAssignmentRepository).delete(assignment);
    }

    @Test
    void removeAssignment_whenAssignmentNotFound_shouldThrow() {
        when(offerRepository.findByUuid("offer-uuid-2")).thenReturn(Optional.of(assignableOffer));
        when(userRepository.findByUuid("user-uuid-1")).thenReturn(Optional.of(user));
        when(offerAssignmentRepository.findByOfferIdAndUserId(2L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> offerService.removeAssignment("offer-uuid-2", "user-uuid-1"))
                .isInstanceOf(OfferNotFoundException.class);
    }
}
