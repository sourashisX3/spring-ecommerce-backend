package com.example.ecommerce_backend.modules.coupon.repository;

import com.example.ecommerce_backend.modules.coupon.entity.CouponUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponUsageRepository extends JpaRepository<CouponUsage, Long> {

    List<CouponUsage> findByCouponId(Long couponId);

    List<CouponUsage> findByUserIdAndCouponId(Long userId, Long couponId);

    long countByCouponId(Long couponId);

    long countByUserIdAndCouponId(Long userId, Long couponId);
}
