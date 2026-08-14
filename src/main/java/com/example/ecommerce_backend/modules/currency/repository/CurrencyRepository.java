package com.example.ecommerce_backend.modules.currency.repository;

import com.example.ecommerce_backend.modules.currency.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {

    Optional<Currency> findByUuid(String uuid);

    Optional<Currency> findByCode(String code);

    boolean existsByCode(String code);

    Optional<Currency> findByIsDefaultTrueAndIsActiveTrue();

    Optional<Currency> findFirstByIsActiveTrueOrderBySortOrderAscIdAsc();

    List<Currency> findByIsActiveTrue();
}
