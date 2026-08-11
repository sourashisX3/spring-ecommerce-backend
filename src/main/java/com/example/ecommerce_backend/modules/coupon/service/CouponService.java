package com.example.ecommerce_backend.modules.coupon.service;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.modules.coupon.dto.request.CouponRequest;
import com.example.ecommerce_backend.modules.coupon.dto.response.CouponResponse;
import com.example.ecommerce_backend.modules.coupon.entity.Coupon;
import com.example.ecommerce_backend.modules.coupon.entity.CouponAssignment;
import com.example.ecommerce_backend.modules.coupon.entity.CouponUsage;
import com.example.ecommerce_backend.modules.coupon.exception.CouponExhaustedException;
import com.example.ecommerce_backend.modules.coupon.exception.CouponExpiredException;
import com.example.ecommerce_backend.modules.coupon.exception.CouponNotFoundException;
import com.example.ecommerce_backend.modules.coupon.mapper.CouponMapper;
import com.example.ecommerce_backend.modules.coupon.repository.CouponAssignmentRepository;
import com.example.ecommerce_backend.modules.coupon.repository.CouponRepository;
import com.example.ecommerce_backend.modules.coupon.repository.CouponUsageRepository;
import com.example.ecommerce_backend.modules.discount.exception.DiscountTypeNotFoundException;
import com.example.ecommerce_backend.modules.discount.repository.DiscountTypeRepository;
import com.example.ecommerce_backend.modules.user.entity.User;
import com.example.ecommerce_backend.modules.user.exception.UserNotFoundException;
import com.example.ecommerce_backend.modules.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponAssignmentRepository couponAssignmentRepository;

    @Autowired
    private CouponUsageRepository couponUsageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DiscountTypeRepository discountTypeRepository;

    private CouponResponse toResponseWithAssignments(Coupon coupon) {
        CouponResponse response = CouponMapper.toResponse(coupon);
        List<String> assignedUserUuids = couponAssignmentRepository.findByCouponId(coupon.getId())
                .stream()
                .map(assignment -> assignment.getUser().getUuid())
                .collect(Collectors.toList());
        response.setAssignedUserUuids(assignedUserUuids);
        return response;
    }

    @Transactional
    @RequiresPermission("coupon:write")
    public CouponResponse create(CouponRequest request) {
        var discountType = discountTypeRepository.findByCode(request.getDiscountTypeCode())
                .orElseThrow(() -> new DiscountTypeNotFoundException(request.getDiscountTypeCode()));

        Coupon coupon = Coupon.builder()
                .code(request.getCode())
                .description(request.getDescription())
                .discountType(discountType)
                .discountValue(request.getDiscountValue())
                .minOrderAmount(request.getMinOrderAmount())
                .maxDiscount(request.getMaxDiscount())
                .usageLimit(request.getUsageLimit())
                .usageLimitPerUser(request.getUsageLimitPerUser())
                .validFrom(request.getValidFrom())
                .validUntil(request.getValidUntil())
                .isGlobal(request.getIsGlobal() != null ? request.getIsGlobal() : true)
                .build();

        coupon = couponRepository.save(coupon);
        return toResponseWithAssignments(coupon);
    }

    @Transactional
    @RequiresPermission("coupon:write")
    public CouponResponse createAssignable(CouponRequest request, List<String> userUuids) {
        var discountType = discountTypeRepository.findByCode(request.getDiscountTypeCode())
                .orElseThrow(() -> new DiscountTypeNotFoundException(request.getDiscountTypeCode()));

        Coupon coupon = Coupon.builder()
                .code(request.getCode())
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
                .build();

        coupon = couponRepository.save(coupon);

        for (String userUuid : userUuids) {
            User user = userRepository.findByUuid(userUuid)
                    .orElseThrow(() -> new UserNotFoundException(userUuid));

            CouponAssignment assignment = CouponAssignment.builder()
                    .coupon(coupon)
                    .user(user)
                    .build();

            couponAssignmentRepository.save(assignment);
        }

        return toResponseWithAssignments(coupon);
    }

    @Transactional(readOnly = true)
    public CouponResponse getByUuid(String uuid) {
        Coupon coupon = couponRepository.findByUuid(uuid)
                .orElseThrow(() -> new CouponNotFoundException(uuid));
        return toResponseWithAssignments(coupon);
    }

    @Transactional(readOnly = true)
    public List<CouponResponse> getAll(Boolean active, Boolean global) {
        List<Coupon> coupons;

        if (active != null) {
            coupons = couponRepository.findByIsActiveTrueAndValidFromLessThanEqualAndValidUntilGreaterThanEqual(
                    Instant.now(), Instant.now());
            if (Boolean.FALSE.equals(active)) {
                List<Coupon> all = couponRepository.findAll();
                coupons = all.stream()
                        .filter(c -> !c.isActive() || c.getValidFrom().isAfter(Instant.now()) || c.getValidUntil().isBefore(Instant.now()))
                        .collect(Collectors.toList());
            }
        } else {
            coupons = couponRepository.findAll();
        }

        if (global != null) {
            coupons = coupons.stream()
                    .filter(c -> c.isGlobal() == global)
                    .collect(Collectors.toList());
        }

        return coupons.stream()
                .map(this::toResponseWithAssignments)
                .collect(Collectors.toList());
    }

    @Transactional
    @RequiresPermission("coupon:write")
    public CouponResponse update(String uuid, CouponRequest request) {
        Coupon coupon = couponRepository.findByUuid(uuid)
                .orElseThrow(() -> new CouponNotFoundException(uuid));

        var discountType = discountTypeRepository.findByCode(request.getDiscountTypeCode())
                .orElseThrow(() -> new DiscountTypeNotFoundException(request.getDiscountTypeCode()));

        coupon.setCode(request.getCode());
        coupon.setDescription(request.getDescription());
        coupon.setDiscountType(discountType);
        coupon.setDiscountValue(request.getDiscountValue());
        coupon.setMinOrderAmount(request.getMinOrderAmount());
        coupon.setMaxDiscount(request.getMaxDiscount());
        coupon.setUsageLimit(request.getUsageLimit());
        coupon.setUsageLimitPerUser(request.getUsageLimitPerUser());
        coupon.setValidFrom(request.getValidFrom());
        coupon.setValidUntil(request.getValidUntil());
        if (request.getIsGlobal() != null) {
            coupon.setGlobal(request.getIsGlobal());
        }

        coupon = couponRepository.save(coupon);
        return toResponseWithAssignments(coupon);
    }

    @Transactional
    @RequiresPermission("coupon:write")
    public void toggleStatus(String uuid, boolean active) {
        Coupon coupon = couponRepository.findByUuid(uuid)
                .orElseThrow(() -> new CouponNotFoundException(uuid));
        coupon.setActive(active);
        couponRepository.save(coupon);
    }

    @Transactional
    @RequiresPermission("coupon:write")
    public void delete(String uuid) {
        Coupon coupon = couponRepository.findByUuid(uuid)
                .orElseThrow(() -> new CouponNotFoundException(uuid));
        couponAssignmentRepository.deleteByCouponId(coupon.getId());
        couponUsageRepository.deleteByCouponId(coupon.getId());
        couponRepository.delete(coupon);
    }

    @Transactional
    @RequiresPermission("coupon:write")
    public void assignToUsers(String couponUuid, List<String> userUuids) {
        Coupon coupon = couponRepository.findByUuid(couponUuid)
                .orElseThrow(() -> new CouponNotFoundException(couponUuid));

        for (String userUuid : userUuids) {
            User user = userRepository.findByUuid(userUuid)
                    .orElseThrow(() -> new UserNotFoundException(userUuid));

            Optional<CouponAssignment> existing = couponAssignmentRepository
                    .findByCouponIdAndUserId(coupon.getId(), user.getId());

            if (existing.isEmpty()) {
                CouponAssignment assignment = CouponAssignment.builder()
                        .coupon(coupon)
                        .user(user)
                        .build();
                couponAssignmentRepository.save(assignment);
            }
        }
    }

    @Transactional
    @RequiresPermission("coupon:write")
    public void removeAssignment(String couponUuid, String userUuid) {
        Coupon coupon = couponRepository.findByUuid(couponUuid)
                .orElseThrow(() -> new CouponNotFoundException(couponUuid));

        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new UserNotFoundException(userUuid));

        CouponAssignment assignment = couponAssignmentRepository
                .findByCouponIdAndUserId(coupon.getId(), user.getId())
                .orElseThrow(() -> new CouponNotFoundException("Assignment not found for coupon: " + couponUuid + " and user: " + userUuid, org.springframework.http.HttpStatus.NOT_FOUND));

        couponAssignmentRepository.delete(assignment);
    }

    @Transactional
    public BigDecimal validateAndApply(String code, Long userId, BigDecimal subtotal, Long orderId) {
        Coupon coupon = couponRepository.findByCodeWithLock(code)
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found with code: " + code, org.springframework.http.HttpStatus.NOT_FOUND));

        Instant now = Instant.now();
        if (!coupon.isActive() || coupon.getValidFrom().isAfter(now) || coupon.getValidUntil().isBefore(now)) {
            throw new CouponExpiredException(code);
        }

        if (!coupon.isGlobal()) {
            couponAssignmentRepository.findByCouponIdAndUserId(coupon.getId(), userId)
                    .orElseThrow(() -> new CouponNotFoundException("Coupon not assigned to user: " + code, org.springframework.http.HttpStatus.NOT_FOUND));
        }

        if (coupon.getUsageLimit() != null && coupon.getTotalUsed() >= coupon.getUsageLimit()) {
            throw new CouponExhaustedException(code);
        }

        if (coupon.getUsageLimitPerUser() != null) {
            long userUsageCount = couponUsageRepository.countByUserIdAndCouponId(userId, coupon.getId());
            if (userUsageCount >= coupon.getUsageLimitPerUser()) {
                throw new CouponExhaustedException(code);
            }
        }

        if (coupon.getMinOrderAmount() != null && subtotal.compareTo(coupon.getMinOrderAmount()) < 0) {
            throw new CouponExpiredException("Minimum order amount of " + coupon.getMinOrderAmount() + " not met for coupon: " + code);
        }

        BigDecimal discount;
        if (coupon.getDiscountType() != null && "PERCENTAGE".equalsIgnoreCase(coupon.getDiscountType().getCode())) {
            discount = subtotal.multiply(coupon.getDiscountValue()).divide(BigDecimal.valueOf(100));
            if (coupon.getMaxDiscount() != null && discount.compareTo(coupon.getMaxDiscount()) > 0) {
                discount = coupon.getMaxDiscount();
            }
        } else {
            discount = coupon.getDiscountValue();
            if (discount.compareTo(subtotal) > 0) {
                discount = subtotal;
            }
        }

        coupon.setTotalUsed(coupon.getTotalUsed() + 1);
        coupon = couponRepository.save(coupon);

        if (!coupon.isGlobal()) {
            couponAssignmentRepository.findByCouponIdAndUserId(coupon.getId(), userId)
                    .ifPresent(assignment -> {
                        assignment.setUsedCount(assignment.getUsedCount() + 1);
                        couponAssignmentRepository.save(assignment);
                    });
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new com.example.ecommerce_backend.modules.user.exception.UserNotFoundException(userId));

        CouponUsage usage = CouponUsage.builder()
                .coupon(coupon)
                .user(user)
                .orderId(orderId)
                .discountAmount(discount)
                .build();

        couponUsageRepository.save(usage);

        return discount;
    }
}
