package com.example.ecommerce_backend.modules.review.service;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.modules.product.entity.Product;
import com.example.ecommerce_backend.modules.product.exception.ProductNotFoundException;
import com.example.ecommerce_backend.modules.product.repository.ProductRepository;
import com.example.ecommerce_backend.modules.review.dto.request.ReviewRequest;
import com.example.ecommerce_backend.modules.review.dto.response.ReviewResponse;
import com.example.ecommerce_backend.modules.review.dto.response.VoteResponse;
import com.example.ecommerce_backend.modules.review.entity.Review;
import com.example.ecommerce_backend.modules.review.entity.ReviewVote;
import com.example.ecommerce_backend.modules.review.exception.ReviewNotFoundException;
import com.example.ecommerce_backend.modules.review.mapper.ReviewMapper;
import com.example.ecommerce_backend.modules.review.repository.ReviewRepository;
import com.example.ecommerce_backend.modules.review.repository.ReviewVoteRepository;
import com.example.ecommerce_backend.modules.user.entity.User;
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
        return reviewRepository.findByProductIdAndIsActiveTrue(product.getId()).stream()
                .map(review -> toResponseWithVotes(review, currentUserId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviews(String productUuid, Pageable pageable, Long currentUserId) {
        Product product = productRepository.findByUuid(productUuid)
                .orElseThrow(() -> new ProductNotFoundException(productUuid));
        Page<Review> reviews = reviewRepository.findByProductIdAndIsActiveTrue(product.getId(), pageable);
        return reviews.map(review -> toResponseWithVotes(review, currentUserId));
    }

    @Transactional(readOnly = true)
    @RequiresPermission("product:write")
    public List<ReviewResponse> listAll() {
        return reviewRepository.findAll().stream()
                .map(review -> toResponseWithVotes(review, null))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @RequiresPermission("product:write")
    public Page<ReviewResponse> listAll(Pageable pageable) {
        return reviewRepository.findAll(pageable)
                .map(review -> toResponseWithVotes(review, null));
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
    public boolean toggleStatus(String uuid, boolean isActive) {
        Review review = reviewRepository.findByUuid(uuid)
                .orElseThrow(() -> new ReviewNotFoundException(uuid));
        if (review.isActive() == isActive) {
            return false;
        }
        review.setActive(isActive);
        reviewRepository.save(review);
        return true;
    }

    @Transactional
    @RequiresPermission("product:write")
    public void deleteReview(String reviewUuid) {
        Review review = reviewRepository.findByUuid(reviewUuid)
                .orElseThrow(() -> new ReviewNotFoundException(reviewUuid));
        reviewRepository.delete(review);
    }

    @Transactional
    public VoteResponse voteReview(String reviewUuid, String voteType, User user) {
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

        return new VoteResponse(resultVoteType, likeCount, dislikeCount);
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
