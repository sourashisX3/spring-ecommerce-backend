package com.example.ecommerce_backend.modules.review.repository;

import com.example.ecommerce_backend.modules.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByProductIdAndIsActiveTrue(Long productId);

    Page<Review> findByProductIdAndIsActiveTrue(Long productId, Pageable pageable);

    List<Review> findByProductId(Long productId);

    Page<Review> findByProductId(Long productId, Pageable pageable);

    Optional<Review> findByUuid(String uuid);

    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r WHERE r.product.id = :productId")
    double getAverageRatingByProductId(Long productId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId")
    long getReviewCountByProductId(Long productId);

    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.product.id = :productId GROUP BY r.rating")
    List<Object[]> getRatingDistributionByProductId(Long productId);

    List<Review> findTop5ByProductIdAndIsActiveTrueOrderByCreatedAtDesc(Long productId);

    long countByUserId(Long userId);
}
