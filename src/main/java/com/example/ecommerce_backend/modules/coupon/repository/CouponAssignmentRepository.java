package com.example.ecommerce_backend.modules.coupon.repository;

import com.example.ecommerce_backend.modules.coupon.entity.CouponAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface CouponAssignmentRepository extends JpaRepository<CouponAssignment, Long> {

    List<CouponAssignment> findByCouponId(Long couponId);

    Optional<CouponAssignment> findByCouponIdAndUserId(Long couponId, Long userId);

    List<CouponAssignment> findByUserId(Long userId);

    void deleteByUserId(Long userId);

    void deleteByCouponId(Long couponId);

    List<CouponAssignment> findByUserIdAndCouponIsActiveTrueAndCouponValidFromLessThanEqualAndCouponValidUntilGreaterThanEqual(
            Long userId, Instant validFrom, Instant validUntil);
}
