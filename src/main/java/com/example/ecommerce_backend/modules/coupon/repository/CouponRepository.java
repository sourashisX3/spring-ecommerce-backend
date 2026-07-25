package com.example.ecommerce_backend.modules.coupon.repository;

import com.example.ecommerce_backend.modules.coupon.entity.Coupon;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Coupon c WHERE c.code = :code")
    Optional<Coupon> findByCodeWithLock(String code);

    Optional<Coupon> findByCode(String code);

    Optional<Coupon> findByUuid(String uuid);

    boolean existsByCode(String code);

    List<Coupon> findByIsActiveTrueAndValidFromLessThanEqualAndValidUntilGreaterThanEqual(
            Instant validFrom, Instant validUntil);
}
