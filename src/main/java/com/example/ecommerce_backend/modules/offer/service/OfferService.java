package com.example.ecommerce_backend.modules.offer.service;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.modules.offer.dto.request.OfferRequest;
import com.example.ecommerce_backend.modules.offer.dto.response.OfferResponse;
import com.example.ecommerce_backend.modules.offer.entity.Offer;
import com.example.ecommerce_backend.modules.offer.entity.OfferAssignment;
import com.example.ecommerce_backend.modules.offer.exception.OfferNotFoundException;
import com.example.ecommerce_backend.modules.offer.mapper.OfferMapper;
import com.example.ecommerce_backend.modules.discount.exception.DiscountTypeNotFoundException;
import com.example.ecommerce_backend.modules.discount.repository.DiscountTypeRepository;
import com.example.ecommerce_backend.modules.offer.repository.OfferAssignmentRepository;
import com.example.ecommerce_backend.modules.offer.repository.OfferRepository;
import com.example.ecommerce_backend.modules.offer.repository.OfferUsageRepository;
import com.example.ecommerce_backend.modules.user.entity.User;
import com.example.ecommerce_backend.modules.user.exception.UserNotFoundException;
import com.example.ecommerce_backend.modules.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OfferService {

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private OfferAssignmentRepository offerAssignmentRepository;

    @Autowired
    private OfferUsageRepository offerUsageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DiscountTypeRepository discountTypeRepository;

    private OfferResponse toResponseWithAssignments(Offer offer) {
        OfferResponse response = OfferMapper.toResponse(offer);
        List<String> assignedUserUuids = offerAssignmentRepository.findByOfferId(offer.getId())
                .stream()
                .map(assignment -> assignment.getUser().getUuid())
                .collect(Collectors.toList());
        response.setAssignedUserUuids(assignedUserUuids);
        return response;
    }

    @Transactional
    @RequiresPermission("offer:write")
    public OfferResponse create(OfferRequest request) {
        var discountType = discountTypeRepository.findByCode(request.getDiscountTypeCode())
                .orElseThrow(() -> new DiscountTypeNotFoundException(request.getDiscountTypeCode()));

        Offer offer = Offer.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .discountType(discountType)
                .discountValue(request.getDiscountValue())
                .minOrderAmount(request.getMinOrderAmount())
                .maxDiscount(request.getMaxDiscount())
                .usageLimit(request.getUsageLimit())
                .usageLimitPerUser(request.getUsageLimitPerUser())
                .validFrom(request.getValidFrom())
                .validUntil(request.getValidUntil())
                .isGlobal(request.getIsGlobal() != null ? request.getIsGlobal() : false)
                .applicableTo(request.getApplicableTo())
                .applicableIds(request.getApplicableIds())
                .build();

        offer = offerRepository.save(offer);
        return toResponseWithAssignments(offer);
    }

    @Transactional
    @RequiresPermission("offer:write")
    public OfferResponse createAssignable(OfferRequest request, List<String> userUuids) {
        var discountType = discountTypeRepository.findByCode(request.getDiscountTypeCode())
                .orElseThrow(() -> new DiscountTypeNotFoundException(request.getDiscountTypeCode()));

        Offer offer = Offer.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .discountType(discountType)
                .discountValue(request.getDiscountValue())
                .minOrderAmount(request.getMinOrderAmount())
                .maxDiscount(request.getMaxDiscount())
                .usageLimit(request.getUsageLimit())
                .usageLimitPerUser(request.getUsageLimitPerUser())
                .validFrom(request.getValidFrom())
                .validUntil(request.getValidUntil())
                .isGlobal(false)
                .applicableTo(request.getApplicableTo())
                .applicableIds(request.getApplicableIds())
                .build();

        offer = offerRepository.save(offer);

        List<User> assignedUsers = new ArrayList<>();
        for (String userUuid : userUuids) {
            User user = userRepository.findByUuid(userUuid)
                    .orElseThrow(() -> new UserNotFoundException(userUuid));

            OfferAssignment assignment = OfferAssignment.builder()
                    .offer(offer)
                    .user(user)
                    .build();

            offerAssignmentRepository.save(assignment);
            assignedUsers.add(user);
        }

        return OfferMapper.toResponse(offer, assignedUsers);
    }

    @Transactional(readOnly = true)
    public OfferResponse getByUuid(String uuid) {
        Offer offer = offerRepository.findByUuid(uuid)
                .orElseThrow(() -> new OfferNotFoundException(uuid));
        return toResponseWithAssignments(offer);
    }

    @Transactional(readOnly = true)
    public List<OfferResponse> getAll(Boolean active, Boolean global) {
        List<Offer> offers;

        if (active != null && global != null) {
            offers = offerRepository.findByIsGlobalAndIsActiveAndValidFromLessThanEqualAndValidUntilGreaterThanEqual(
                    global, active, Instant.now(), Instant.now());
        } else if (active != null) {
            offers = offerRepository.findByIsActiveTrueAndValidFromLessThanEqualAndValidUntilGreaterThanEqual(
                    Instant.now(), Instant.now());
        } else if (global != null) {
            offers = offerRepository.findAll().stream()
                    .filter(o -> o.isGlobal() == global)
                    .collect(Collectors.toList());
        } else {
            offers = offerRepository.findAll();
        }

        return offers.stream()
                .map(this::toResponseWithAssignments)
                .collect(Collectors.toList());
    }

    @Transactional
    @RequiresPermission("offer:write")
    public OfferResponse update(String uuid, OfferRequest request) {
        Offer offer = offerRepository.findByUuid(uuid)
                .orElseThrow(() -> new OfferNotFoundException(uuid));

        var discountType = discountTypeRepository.findByCode(request.getDiscountTypeCode())
                .orElseThrow(() -> new DiscountTypeNotFoundException(request.getDiscountTypeCode()));

        offer.setTitle(request.getTitle());
        offer.setDescription(request.getDescription());
        offer.setDiscountType(discountType);
        offer.setDiscountValue(request.getDiscountValue());
        offer.setMinOrderAmount(request.getMinOrderAmount());
        offer.setMaxDiscount(request.getMaxDiscount());
        offer.setUsageLimit(request.getUsageLimit());
        offer.setUsageLimitPerUser(request.getUsageLimitPerUser());
        offer.setValidFrom(request.getValidFrom());
        offer.setValidUntil(request.getValidUntil());
        if (request.getIsGlobal() != null) {
            offer.setGlobal(request.getIsGlobal());
        }
        offer.setApplicableTo(request.getApplicableTo());
        offer.setApplicableIds(request.getApplicableIds());

        offer = offerRepository.save(offer);
        return toResponseWithAssignments(offer);
    }

    @Transactional
    @RequiresPermission("offer:write")
    public void toggleStatus(String uuid, boolean active) {
        Offer offer = offerRepository.findByUuid(uuid)
                .orElseThrow(() -> new OfferNotFoundException(uuid));
        offer.setActive(active);
        offerRepository.save(offer);
    }

    @Transactional
    @RequiresPermission("offer:write")
    public void delete(String uuid) {
        Offer offer = offerRepository.findByUuid(uuid)
                .orElseThrow(() -> new OfferNotFoundException(uuid));
        offerAssignmentRepository.deleteByOfferId(offer.getId());
        offerUsageRepository.deleteByOfferId(offer.getId());
        offerRepository.delete(offer);
    }

    @Transactional(readOnly = true)
    public List<OfferResponse> getEligibleOffers(Long userId) {
        Instant now = Instant.now();

        List<Offer> globalOffers = offerRepository
                .findByIsGlobalAndIsActiveAndValidFromLessThanEqualAndValidUntilGreaterThanEqual(
                        true, true, now, now);

        List<OfferAssignment> assignments = offerAssignmentRepository
                .findByUserIdAndOfferIsActiveTrueAndOfferValidFromLessThanEqualAndOfferValidUntilGreaterThanEqual(
                        userId, now, now);

        List<Offer> userOffers = assignments.stream()
                .map(OfferAssignment::getOffer)
                .collect(Collectors.toList());

        List<Offer> allEligible = new ArrayList<>();
        allEligible.addAll(globalOffers);
        allEligible.addAll(userOffers);

        return allEligible.stream()
                .map(OfferMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @RequiresPermission("offer:write")
    public void assignToUsers(String offerUuid, List<String> userUuids, Integer usageLimitPerUser) {
        Offer offer = offerRepository.findByUuid(offerUuid)
                .orElseThrow(() -> new OfferNotFoundException(offerUuid));

        for (String userUuid : userUuids) {
            User user = userRepository.findByUuid(userUuid)
                    .orElseThrow(() -> new UserNotFoundException(userUuid));

            Optional<OfferAssignment> existing = offerAssignmentRepository
                    .findByOfferIdAndUserId(offer.getId(), user.getId());

            if (existing.isEmpty()) {
                OfferAssignment assignment = OfferAssignment.builder()
                        .offer(offer)
                        .user(user)
                        .build();
                offerAssignmentRepository.save(assignment);
            }
        }
    }

    @Transactional
    @RequiresPermission("offer:write")
    public void removeAssignment(String offerUuid, String userUuid) {
        Offer offer = offerRepository.findByUuid(offerUuid)
                .orElseThrow(() -> new OfferNotFoundException(offerUuid));

        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new UserNotFoundException(userUuid));

        OfferAssignment assignment = offerAssignmentRepository
                .findByOfferIdAndUserId(offer.getId(), user.getId())
                .orElseThrow(() -> new OfferNotFoundException("Assignment not found for offer: " + offerUuid + " and user: " + userUuid));

        offerAssignmentRepository.delete(assignment);
    }

}
