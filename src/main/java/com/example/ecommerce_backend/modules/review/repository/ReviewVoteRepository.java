package com.example.ecommerce_backend.modules.review.repository;

import com.example.ecommerce_backend.modules.review.entity.ReviewVote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewVoteRepository extends JpaRepository<ReviewVote, Long> {

    Optional<ReviewVote> findByReviewIdAndUserId(Long reviewId, Long userId);

    long countByReviewIdAndVoteType(Long reviewId, String voteType);
}
