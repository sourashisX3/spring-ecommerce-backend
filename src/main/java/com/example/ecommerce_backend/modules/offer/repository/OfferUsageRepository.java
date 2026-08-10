package com.example.ecommerce_backend.modules.offer.repository;

import com.example.ecommerce_backend.modules.offer.entity.OfferUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OfferUsageRepository extends JpaRepository<OfferUsage, Long> {

    List<OfferUsage> findByOfferId(Long offerId);

    List<OfferUsage> findByUserIdAndOfferId(Long userId, Long offerId);

    long countByOfferId(Long offerId);

    long countByUserIdAndOfferId(Long userId, Long offerId);

    void deleteByUserId(Long userId);
}
