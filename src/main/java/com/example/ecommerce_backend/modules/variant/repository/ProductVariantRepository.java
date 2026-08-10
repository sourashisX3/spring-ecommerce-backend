package com.example.ecommerce_backend.modules.variant.repository;

import com.example.ecommerce_backend.modules.variant.entity.ProductVariant;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    Optional<ProductVariant> findByUuid(String uuid);

    Optional<ProductVariant> findBySku(String sku);

    boolean existsBySku(String sku);

    @Query("SELECT COALESCE(SUM(v.stock), 0) FROM ProductVariant v WHERE v.product.id = :productId AND v.isActive = true")
    int getTotalStockByProductId(Long productId);

    @Query("SELECT v FROM ProductVariant v JOIN FETCH v.product p WHERE v.stock <= :threshold AND v.isActive = true ORDER BY v.stock ASC")
    List<ProductVariant> findLowStock(@Param("threshold") int threshold, Pageable pageable);
}
