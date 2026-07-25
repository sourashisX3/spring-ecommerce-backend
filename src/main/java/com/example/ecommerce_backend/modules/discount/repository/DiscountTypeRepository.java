package com.example.ecommerce_backend.modules.discount.repository;

import com.example.ecommerce_backend.modules.discount.entity.DiscountType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DiscountTypeRepository extends JpaRepository<DiscountType, Long> {
    Optional<DiscountType> findByUuid(String uuid);
    Optional<DiscountType> findByCode(String code);
    boolean existsByCode(String code);
}
