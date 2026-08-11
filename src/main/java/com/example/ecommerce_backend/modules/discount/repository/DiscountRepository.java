package com.example.ecommerce_backend.modules.discount.repository;

import com.example.ecommerce_backend.modules.discount.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface DiscountRepository extends JpaRepository<Discount, Long> {
    Optional<Discount> findByUuid(String uuid);
    List<Discount> findByIsGlobalAndIsActiveAndValidFromLessThanEqualAndValidUntilGreaterThanEqual(
            boolean isGlobal, boolean isActive, Instant validFrom, Instant validUntil);
    List<Discount> findByIsActiveAndValidFromLessThanEqualAndValidUntilGreaterThanEqual(
            boolean isActive, Instant validFrom, Instant validUntil);
}
