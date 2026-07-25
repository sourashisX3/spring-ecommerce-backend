package com.example.ecommerce_backend.modules.product.repository;

import com.example.ecommerce_backend.modules.product.entity.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @EntityGraph(attributePaths = {"parent"})
    List<Category> findAll();

    Optional<Category> findBySlug(String slug);

    boolean existsBySlug(String slug);

    @Query(value = """
            WITH RECURSIVE category_tree(id) AS (
                SELECT id FROM categories WHERE slug = :slug
                UNION ALL
                SELECT c.id FROM categories c
                INNER JOIN category_tree ct ON c.parent_id = ct.id
            )
            SELECT id FROM category_tree
            """, nativeQuery = true)
    Set<Long> findDescendantIds(@Param("slug") String slug);
}
