package com.example.ecommerce_backend.modules.offer.repository;

import com.example.ecommerce_backend.modules.offer.entity.OfferAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface OfferAssignmentRepository extends JpaRepository<OfferAssignment, Long> {

    List<OfferAssignment> findByOfferId(Long offerId);

    Optional<OfferAssignment> findByOfferIdAndUserId(Long offerId, Long userId);

    List<OfferAssignment> findByUserId(Long userId);

    long countByOfferId(Long offerId);

    List<OfferAssignment> findByUserIdAndOfferIsActiveTrueAndOfferValidFromLessThanEqualAndOfferValidUntilGreaterThanEqual(
            Long userId, Instant validFrom, Instant validUntil);
}
