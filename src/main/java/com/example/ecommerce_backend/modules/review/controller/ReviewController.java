package com.example.ecommerce_backend.modules.review.controller;

import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.core.dto.Pagination;
import com.example.ecommerce_backend.modules.review.dto.request.ReviewRequest;
import com.example.ecommerce_backend.modules.review.dto.request.VoteRequest;
import com.example.ecommerce_backend.modules.review.dto.response.ReviewResponse;
import com.example.ecommerce_backend.modules.review.dto.response.VoteResponse;
import com.example.ecommerce_backend.modules.review.service.ReviewService;
import com.example.ecommerce_backend.modules.user.entity.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/products/{productUuid}/reviews")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getReviews(
            @PathVariable String productUuid,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @AuthenticationPrincipal User user
    ) {
        Long userId = user != null ? user.getId() : null;

        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<ReviewResponse> reviews = reviewService.getReviews(productUuid, pageable, userId);
            return ApiResponse.paginated(
                    reviews.getContent(),
                    "Reviews retrieved successfully",
                    Pagination.of(reviews)
            );
        } else {
            List<ReviewResponse> reviews = reviewService.getReviews(productUuid, userId);
            return ApiResponse.success(reviews, "Reviews retrieved successfully");
        }
    }

    @PostMapping("/products/{productUuid}/reviews")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @PathVariable String productUuid,
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal User user
    ) {
        ReviewResponse review = reviewService.createReview(productUuid, request, user);
        return ApiResponse.created(review, "Review created successfully");
    }

    @DeleteMapping("/reviews/{reviewUuid}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable String reviewUuid) {
        reviewService.deleteReview(reviewUuid);
        return ApiResponse.success(null, "Review deleted successfully");
    }

    @PostMapping("/reviews/{reviewUuid}/vote")
    public ResponseEntity<ApiResponse<VoteResponse>> voteReview(
            @PathVariable String reviewUuid,
            @Valid @RequestBody VoteRequest request,
            @AuthenticationPrincipal User user
    ) {
        var result = reviewService.voteReview(reviewUuid, request.getVoteType(), user);
        return ApiResponse.success(result, "Vote updated successfully");
    }
}
