package com.example.ecommerce_backend.modules.variant.repository;

import com.example.ecommerce_backend.modules.variant.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    Optional<ProductVariant> findByUuid(String uuid);

    boolean existsBySku(String sku);
}
