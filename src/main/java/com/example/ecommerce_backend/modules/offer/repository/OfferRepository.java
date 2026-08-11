package com.example.ecommerce_backend.modules.offer.repository;

import com.example.ecommerce_backend.modules.offer.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface OfferRepository extends JpaRepository<Offer, Long> {

    Optional<Offer> findByUuid(String uuid);

    List<Offer> findByIsGlobalAndIsActiveAndValidFromLessThanEqualAndValidUntilGreaterThanEqual(
            boolean isGlobal, boolean isActive, Instant validFrom, Instant validUntil);

    List<Offer> findByIsActiveAndValidFromLessThanEqualAndValidUntilGreaterThanEqual(
            boolean isActive, Instant validFrom, Instant validUntil);

    boolean existsByTitle(String title);
}
