package com.example.ecommerce_backend.modules.review.mapper;

import com.example.ecommerce_backend.modules.review.dto.response.ReviewResponse;
import com.example.ecommerce_backend.modules.review.entity.Review;

public class ReviewMapper {

    private ReviewMapper() {
    }

    public static ReviewResponse toResponse(Review review, long likeCount, long dislikeCount, String currentUserVote) {
        return ReviewResponse.builder()
                .id(review.getId())
                .uuid(review.getUuid())
                .productUuid(review.getProduct().getUuid())
                .productName(review.getProduct().getName())
                .rating(review.getRating())
                .title(review.getTitle())
                .comment(review.getComment())
                .isActive(review.isActive())
                .isVerifiedPurchase(review.isVerifiedPurchase())
                .userFirstName(review.getUser().getFirstName())
                .userLastName(review.getUser().getLastName())
                .likeCount(likeCount)
                .dislikeCount(dislikeCount)
                .currentUserVote(currentUserVote)
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
