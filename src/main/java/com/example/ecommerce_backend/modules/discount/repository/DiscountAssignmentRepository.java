package com.example.ecommerce_backend.modules.discount.repository;

import com.example.ecommerce_backend.modules.discount.entity.DiscountAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface DiscountAssignmentRepository extends JpaRepository<DiscountAssignment, Long> {
    List<DiscountAssignment> findByDiscountId(Long discountId);
    Optional<DiscountAssignment> findByDiscountIdAndUserId(Long discountId, Long userId);
    List<DiscountAssignment> findByUserId(Long userId);
    void deleteByUserId(Long userId);
    List<DiscountAssignment> findByUserIdAndDiscountIsActiveTrueAndDiscountValidFromLessThanEqualAndDiscountValidUntilGreaterThanEqual(
            Long userId, Instant validFrom, Instant validUntil);
}
