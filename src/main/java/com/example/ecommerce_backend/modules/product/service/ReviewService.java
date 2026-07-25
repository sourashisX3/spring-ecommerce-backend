package com.example.ecommerce_backend.modules.product.service;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.modules.product.dto.request.ReviewRequest;
import com.example.ecommerce_backend.modules.product.dto.response.ReviewResponse;
import com.example.ecommerce_backend.modules.product.entity.Product;
import com.example.ecommerce_backend.modules.product.entity.Review;
import com.example.ecommerce_backend.modules.product.entity.ReviewVote;
import com.example.ecommerce_backend.modules.product.exception.ProductNotFoundException;
import com.example.ecommerce_backend.modules.product.exception.ReviewNotFoundException;
import com.example.ecommerce_backend.modules.product.mapper.ReviewMapper;
import com.example.ecommerce_backend.modules.product.repository.ProductRepository;
import com.example.ecommerce_backend.modules.product.repository.ReviewRepository;
import com.example.ecommerce_backend.modules.product.repository.ReviewVoteRepository;
import com.example.ecommerce_backend.modules.role_user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewVoteRepository reviewVoteRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviews(String productUuid, Long currentUserId) {
        Product product = productRepository.findByUuid(productUuid)
                .orElseThrow(() -> new ProductNotFoundException(productUuid));
        return reviewRepository.findByProductId(product.getId()).stream()
                .map(review -> toResponseWithVotes(review, currentUserId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviews(String productUuid, Pageable pageable, Long currentUserId) {
        Product product = productRepository.findByUuid(productUuid)
                .orElseThrow(() -> new ProductNotFoundException(productUuid));
        return reviewRepository.findByProductId(product.getId(), pageable)
                .map(review -> toResponseWithVotes(review, currentUserId));
    }

    @Transactional
    public ReviewResponse createReview(String productUuid, ReviewRequest request, User user) {
        Product product = productRepository.findByUuid(productUuid)
                .orElseThrow(() -> new ProductNotFoundException(productUuid));

        Review review = Review.builder()
                .product(product)
                .user(user)
                .rating(request.getRating())
                .title(request.getTitle())
                .comment(request.getComment())
                .build();

        review = reviewRepository.save(review);
        return toResponseWithVotes(review, user.getId());
    }

    @Transactional
    @RequiresPermission("product:write")
    public void deleteReview(String reviewUuid) {
        Review review = reviewRepository.findByUuid(reviewUuid)
                .orElseThrow(() -> new ReviewNotFoundException(reviewUuid));
        reviewRepository.delete(review);
    }

    @Transactional
    public com.example.ecommerce_backend.modules.product.dto.response.VoteResponse voteReview(String reviewUuid, String voteType, User user) {
        Review review = reviewRepository.findByUuid(reviewUuid)
                .orElseThrow(() -> new ReviewNotFoundException(reviewUuid));

        Optional<ReviewVote> existing = reviewVoteRepository.findByReviewIdAndUserId(review.getId(), user.getId());
        String resultVoteType = voteType;

        if (existing.isPresent()) {
            ReviewVote vote = existing.get();
            if (vote.getVoteType().equals(voteType)) {
                reviewVoteRepository.delete(vote);
                resultVoteType = null;
            } else {
                vote.setVoteType(voteType);
                reviewVoteRepository.save(vote);
            }
        } else {
            ReviewVote vote = ReviewVote.builder()
                    .review(review)
                    .user(user)
                    .voteType(voteType)
                    .build();
            reviewVoteRepository.save(vote);
        }

        long likeCount = reviewVoteRepository.countByReviewIdAndVoteType(review.getId(), "like");
        long dislikeCount = reviewVoteRepository.countByReviewIdAndVoteType(review.getId(), "dislike");

        return new com.example.ecommerce_backend.modules.product.dto.response.VoteResponse(resultVoteType, likeCount, dislikeCount);
    }

    private ReviewResponse toResponseWithVotes(Review review, Long currentUserId) {
        long likeCount = reviewVoteRepository.countByReviewIdAndVoteType(review.getId(), "like");
        long dislikeCount = reviewVoteRepository.countByReviewIdAndVoteType(review.getId(), "dislike");

        String currentUserVote = null;
        if (currentUserId != null) {
            currentUserVote = reviewVoteRepository.findByReviewIdAndUserId(review.getId(), currentUserId)
                    .map(ReviewVote::getVoteType)
                    .orElse(null);
        }

        return ReviewMapper.toResponse(review, likeCount, dislikeCount, currentUserVote);
    }
}