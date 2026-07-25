package com.example.ecommerce_backend.modules.brand.repository;

import com.example.ecommerce_backend.modules.brand.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {

    Optional<Brand> findBySlug(String slug);

    Optional<Brand> findByUuid(String uuid);

    boolean existsBySlug(String slug);
}
