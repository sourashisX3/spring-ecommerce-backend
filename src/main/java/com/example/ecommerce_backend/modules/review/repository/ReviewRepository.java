package com.example.ecommerce_backend.modules.review.repository;

import com.example.ecommerce_backend.modules.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByProductId(Long productId);

    Page<Review> findByProductId(Long productId, Pageable pageable);

    Optional<Review> findByUuid(String uuid);
}
