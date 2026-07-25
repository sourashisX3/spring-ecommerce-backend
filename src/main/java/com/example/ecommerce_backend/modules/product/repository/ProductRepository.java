package com.example.ecommerce_backend.modules.product.repository;

import com.example.ecommerce_backend.modules.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Optional<Product> findByUuid(String uuid);

    Optional<Product> findBySlug(String slug);

    boolean existsBySku(String sku);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId")
    long countByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.brand.id = :brandId")
    long countByBrandId(@Param("brandId") Long brandId);

}
