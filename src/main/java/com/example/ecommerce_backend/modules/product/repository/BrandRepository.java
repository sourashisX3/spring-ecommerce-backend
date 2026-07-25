package com.example.ecommerce_backend.modules.product.repository;

import com.example.ecommerce_backend.modules.product.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {

    Optional<Brand> findBySlug(String slug);

    boolean existsBySlug(String slug);
}
