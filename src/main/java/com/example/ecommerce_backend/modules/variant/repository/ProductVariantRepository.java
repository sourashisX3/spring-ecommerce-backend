package com.example.ecommerce_backend.modules.variant.repository;

import com.example.ecommerce_backend.modules.variant.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    Optional<ProductVariant> findByUuid(String uuid);

    Optional<ProductVariant> findBySku(String sku);

    boolean existsBySku(String sku);

    @Query("SELECT COALESCE(SUM(v.stock), 0) FROM ProductVariant v WHERE v.product.id = :productId AND v.isActive = true")
    int getTotalStockByProductId(Long productId);
}
