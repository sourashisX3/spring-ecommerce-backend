package com.example.ecommerce_backend.modules.tag.repository;

import com.example.ecommerce_backend.modules.tag.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findBySlug(String slug);

    Optional<Tag> findByUuid(String uuid);

    boolean existsBySlug(String slug);

    @Query("""
            SELECT t FROM Tag t
            WHERE (:active IS NULL OR t.isActive = :active)
              AND (:search IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(t.slug) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<Tag> search(@Param("search") String search, @Param("active") Boolean active, Pageable pageable);
}
