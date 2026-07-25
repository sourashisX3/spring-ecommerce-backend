package com.example.ecommerce_backend.modules.tag.repository;

import com.example.ecommerce_backend.modules.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findBySlug(String slug);

    Optional<Tag> findByUuid(String uuid);

    boolean existsBySlug(String slug);
}
