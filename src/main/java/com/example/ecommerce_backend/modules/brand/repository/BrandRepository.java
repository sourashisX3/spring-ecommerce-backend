package com.example.ecommerce_backend.modules.brand.repository;

import com.example.ecommerce_backend.modules.brand.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {

    Optional<Brand> findBySlug(String slug);

    Optional<Brand> findByUuid(String uuid);

    boolean existsBySlug(String slug);

    @Query("""
            SELECT b FROM Brand b
            WHERE (:active IS NULL OR b.isActive = :active)
              AND (:search IS NULL OR LOWER(b.name) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(b.slug) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<Brand> search(@Param("search") String search, @Param("active") Boolean active, Pageable pageable);
}
