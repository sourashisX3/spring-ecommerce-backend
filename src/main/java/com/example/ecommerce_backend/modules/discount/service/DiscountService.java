package com.example.ecommerce_backend.modules.discount.service;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.modules.discount.dto.request.DiscountRequest;
import com.example.ecommerce_backend.modules.discount.dto.response.DiscountResponse;
import com.example.ecommerce_backend.modules.discount.entity.Discount;
import com.example.ecommerce_backend.modules.discount.entity.DiscountAssignment;
import com.example.ecommerce_backend.modules.discount.entity.DiscountType;
import com.example.ecommerce_backend.modules.discount.exception.DiscountNotFoundException;
import com.example.ecommerce_backend.modules.discount.exception.DiscountTypeNotFoundException;
import com.example.ecommerce_backend.modules.discount.mapper.DiscountMapper;
import com.example.ecommerce_backend.modules.discount.repository.DiscountAssignmentRepository;
import com.example.ecommerce_backend.modules.discount.repository.DiscountRepository;
import com.example.ecommerce_backend.modules.discount.repository.DiscountTypeRepository;
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
public class DiscountService {

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private DiscountTypeRepository discountTypeRepository;

    @Autowired
    private DiscountAssignmentRepository discountAssignmentRepository;

    @Autowired
    private UserRepository userRepository;

    private List<String> getAssignedUserUuids(Discount discount) {
        return discountAssignmentRepository.findByDiscountId(discount.getId())
                .stream()
                .map(assignment -> assignment.getUser().getUuid())
                .collect(Collectors.toList());
    }

    private DiscountResponse toResponseWithAssignments(Discount discount) {
        return DiscountMapper.toResponse(discount, getAssignedUserUuids(discount));
    }

    @Transactional
    @RequiresPermission("discount:write")
    public DiscountResponse create(DiscountRequest request) {
        DiscountType type = discountTypeRepository.findByCode(request.getDiscountTypeCode())
                .orElseThrow(() -> new DiscountTypeNotFoundException(request.getDiscountTypeCode()));

        Discount discount = Discount.builder()
                .discountType(type)
                .discountValue(request.getDiscountValue())
                .minOrderAmount(request.getMinOrderAmount())
                .maxDiscount(request.getMaxDiscount())
                .isGlobal(request.getIsGlobal() != null ? request.getIsGlobal() : false)
                .validFrom(request.getValidFrom())
                .validUntil(request.getValidUntil())
                .description(request.getDescription())
                .build();

        discount = discountRepository.save(discount);
        return toResponseWithAssignments(discount);
    }

    @Transactional
    @RequiresPermission("discount:write")
    public DiscountResponse createAssignable(DiscountRequest request, List<String> userUuids) {
        DiscountResponse response = create(request);

        Discount discount = discountRepository.findByUuid(response.getUuid())
                .orElseThrow(() -> new DiscountNotFoundException(response.getUuid()));

        for (String userUuid : userUuids) {
            User user = userRepository.findByUuid(userUuid)
                    .orElseThrow(() -> new UserNotFoundException(userUuid));

            DiscountAssignment assignment = DiscountAssignment.builder()
                    .discount(discount)
                    .user(user)
                    .build();

            discountAssignmentRepository.save(assignment);
        }

        return toResponseWithAssignments(discount);
    }

    @Transactional(readOnly = true)
    public DiscountResponse getByUuid(String uuid) {
        Discount discount = discountRepository.findByUuid(uuid)
                .orElseThrow(() -> new DiscountNotFoundException(uuid));
        return toResponseWithAssignments(discount);
    }

    @Transactional(readOnly = true)
    public List<DiscountResponse> getAll(Boolean active, Boolean global) {
        return getAll(active, global, null);
    }

    @Transactional(readOnly = true)
    public List<DiscountResponse> getAll(Boolean active, Boolean global, String search) {
        List<Discount> discounts;

        if (active != null && global != null) {
            discounts = discountRepository.findByIsGlobalAndIsActiveAndValidFromLessThanEqualAndValidUntilGreaterThanEqual(
                    global, active, Instant.now(), Instant.now());
        } else if (active != null) {
            discounts = discountRepository.findByIsActiveAndValidFromLessThanEqualAndValidUntilGreaterThanEqual(
                    active, Instant.now(), Instant.now());
        } else if (global != null) {
            discounts = discountRepository.findAll().stream()
                    .filter(d -> d.isGlobal() == global)
                    .collect(Collectors.toList());
        } else {
            discounts = discountRepository.findAll();
        }

        if (search != null && !search.isBlank()) {
            String q = search.trim().toLowerCase();
            discounts = discounts.stream()
                    .filter(d -> (d.getDescription() != null && d.getDescription().toLowerCase().contains(q))
                            || (d.getDiscountType() != null && d.getDiscountType().getName() != null
                                    && d.getDiscountType().getName().toLowerCase().contains(q)))
                    .collect(Collectors.toList());
        }

        return discounts.stream()
                .map(this::toResponseWithAssignments)
                .collect(Collectors.toList());
    }

    @Transactional
    @RequiresPermission("discount:write")
    public DiscountResponse update(String uuid, DiscountRequest request) {
        Discount discount = discountRepository.findByUuid(uuid)
                .orElseThrow(() -> new DiscountNotFoundException(uuid));

        DiscountType type = discountTypeRepository.findByCode(request.getDiscountTypeCode())
                .orElseThrow(() -> new DiscountTypeNotFoundException(request.getDiscountTypeCode()));

        discount.setDiscountType(type);
        discount.setDiscountValue(request.getDiscountValue());
        discount.setMinOrderAmount(request.getMinOrderAmount());
        discount.setMaxDiscount(request.getMaxDiscount());
        if (request.getIsGlobal() != null) {
            discount.setGlobal(request.getIsGlobal());
        }
        discount.setValidFrom(request.getValidFrom());
        discount.setValidUntil(request.getValidUntil());
        discount.setDescription(request.getDescription());

        discount = discountRepository.save(discount);
        return toResponseWithAssignments(discount);
    }

    @Transactional
    @RequiresPermission("discount:write")
    public void toggleStatus(String uuid, boolean active) {
        Discount discount = discountRepository.findByUuid(uuid)
                .orElseThrow(() -> new DiscountNotFoundException(uuid));
        discount.setActive(active);
        discountRepository.save(discount);
    }

    @Transactional
    @RequiresPermission("discount:write")
    public void delete(String uuid) {
        Discount discount = discountRepository.findByUuid(uuid)
                .orElseThrow(() -> new DiscountNotFoundException(uuid));
        discountAssignmentRepository.deleteByDiscountId(discount.getId());
        discountRepository.delete(discount);
    }

    @Transactional(readOnly = true)
    public List<DiscountResponse> getEligibleDiscounts(Long userId) {
        Instant now = Instant.now();

        List<Discount> globalDiscounts = discountRepository
                .findByIsGlobalAndIsActiveAndValidFromLessThanEqualAndValidUntilGreaterThanEqual(
                        true, true, now, now);

        List<DiscountAssignment> assignments = discountAssignmentRepository
                .findByUserIdAndDiscountIsActiveTrueAndDiscountValidFromLessThanEqualAndDiscountValidUntilGreaterThanEqual(
                        userId, now, now);

        List<Discount> userDiscounts = assignments.stream()
                .map(DiscountAssignment::getDiscount)
                .collect(Collectors.toList());

        List<Discount> allEligible = new ArrayList<>();
        allEligible.addAll(globalDiscounts);
        allEligible.addAll(userDiscounts);

        return allEligible.stream()
                .map(DiscountMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @RequiresPermission("discount:write")
    public void assignToUsers(String discountUuid, List<String> userUuids) {
        Discount discount = discountRepository.findByUuid(discountUuid)
                .orElseThrow(() -> new DiscountNotFoundException(discountUuid));

        for (String userUuid : userUuids) {
            User user = userRepository.findByUuid(userUuid)
                    .orElseThrow(() -> new UserNotFoundException(userUuid));

            Optional<DiscountAssignment> existing = discountAssignmentRepository
                    .findByDiscountIdAndUserId(discount.getId(), user.getId());

            if (existing.isEmpty()) {
                DiscountAssignment assignment = DiscountAssignment.builder()
                        .discount(discount)
                        .user(user)
                        .build();
                discountAssignmentRepository.save(assignment);
            }
        }
    }

    @Transactional
    @RequiresPermission("discount:write")
    public void removeAssignment(String discountUuid, String userUuid) {
        Discount discount = discountRepository.findByUuid(discountUuid)
                .orElseThrow(() -> new DiscountNotFoundException(discountUuid));

        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new UserNotFoundException(userUuid));

        DiscountAssignment assignment = discountAssignmentRepository
                .findByDiscountIdAndUserId(discount.getId(), user.getId())
                .orElseThrow(() -> new DiscountNotFoundException("Assignment not found"));

        discountAssignmentRepository.delete(assignment);
    }
}
