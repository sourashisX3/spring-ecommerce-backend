package com.example.ecommerce_backend.modules.review.service;

import com.example.ecommerce_backend.modules.product.entity.Product;
import com.example.ecommerce_backend.modules.product.exception.ProductNotFoundException;
import com.example.ecommerce_backend.modules.product.repository.ProductRepository;
import com.example.ecommerce_backend.modules.review.dto.request.ReviewRequest;
import com.example.ecommerce_backend.modules.review.dto.response.ReviewResponse;
import com.example.ecommerce_backend.modules.review.dto.response.VoteResponse;
import com.example.ecommerce_backend.modules.review.entity.Review;
import com.example.ecommerce_backend.modules.review.entity.ReviewVote;
import com.example.ecommerce_backend.modules.review.exception.ReviewNotFoundException;
import com.example.ecommerce_backend.modules.review.repository.ReviewRepository;
import com.example.ecommerce_backend.modules.review.repository.ReviewVoteRepository;
import com.example.ecommerce_backend.modules.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewVoteRepository reviewVoteRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ReviewService reviewService;

    private Product product;
    private User user;
    private Review review;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L).uuid("product-uuid").name("Test Product")
                .slug("test-product").basePrice(BigDecimal.TEN).isActive(true)
                .variants(List.of()).images(List.of()).tags(new java.util.HashSet<>())
                .build();

        user = User.builder().id(1L).firstName("John").lastName("Doe").email("john@test.com").build();

        review = Review.builder()
                .id(1L).uuid("review-uuid").product(product).user(user)
                .rating(5).title("Great").comment("Love it")
                .isActive(true).isVerifiedPurchase(false)
                .build();
    }

    // --- getReviews (list) ---

    @Test
    void getReviews_shouldReturnList() {
        when(productRepository.findByUuid("product-uuid")).thenReturn(Optional.of(product));
        when(reviewRepository.findByProductId(1L)).thenReturn(List.of(review));
        when(reviewVoteRepository.countByReviewIdAndVoteType(1L, "like")).thenReturn(3L);
        when(reviewVoteRepository.countByReviewIdAndVoteType(1L, "dislike")).thenReturn(1L);
        when(reviewVoteRepository.findByReviewIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        List<ReviewResponse> result = reviewService.getReviews("product-uuid", 1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRating()).isEqualTo(5);
        assertThat(result.get(0).getLikeCount()).isEqualTo(3);
        assertThat(result.get(0).getDislikeCount()).isEqualTo(1);
    }

    @Test
    void getReviews_shouldFilterInactiveReviews() {
        Review inactiveReview = Review.builder()
                .id(2L).uuid("inactive-review-uuid").product(product).user(user)
                .rating(3).title("Old").comment("Meh")
                .isActive(false).isVerifiedPurchase(false)
                .build();
        when(productRepository.findByUuid("product-uuid")).thenReturn(Optional.of(product));
        when(reviewRepository.findByProductId(1L)).thenReturn(List.of(review, inactiveReview));
        when(reviewVoteRepository.countByReviewIdAndVoteType(1L, "like")).thenReturn(0L);
        when(reviewVoteRepository.countByReviewIdAndVoteType(1L, "dislike")).thenReturn(0L);
        when(reviewVoteRepository.findByReviewIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        List<ReviewResponse> result = reviewService.getReviews("product-uuid", 1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUuid()).isEqualTo("review-uuid");
    }

    @Test
    void getReviews_whenProductNotFound_shouldThrow() {
        when(productRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.getReviews("nonexistent", null))
                .isInstanceOf(ProductNotFoundException.class);
    }

    // --- getReviews (pageable) ---

    @Test
    void getReviews_withPageable_shouldReturnPage() {
        when(productRepository.findByUuid("product-uuid")).thenReturn(Optional.of(product));
        when(reviewRepository.findByProductId(eq(1L), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(review)));
        when(reviewVoteRepository.countByReviewIdAndVoteType(1L, "like")).thenReturn(3L);
        when(reviewVoteRepository.countByReviewIdAndVoteType(1L, "dislike")).thenReturn(1L);

        Page<ReviewResponse> result = reviewService.getReviews("product-uuid", PageRequest.of(0, 10), null);

        assertThat(result.getContent()).hasSize(1);
    }

    // --- createReview ---

    @Test
    void createReview_shouldSaveAndReturn() {
        ReviewRequest request = new ReviewRequest();
        request.setRating(4);
        request.setTitle("Good");
        request.setComment("Nice");

        when(productRepository.findByUuid("product-uuid")).thenReturn(Optional.of(product));
        when(reviewRepository.save(any())).thenAnswer(invocation -> {
            Review r = invocation.getArgument(0);
            r.setId(2L);
            r.setUuid("new-uuid");
            return r;
        });
        when(reviewVoteRepository.countByReviewIdAndVoteType(anyLong(), anyString())).thenReturn(0L);
        when(reviewVoteRepository.findByReviewIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());

        ReviewResponse result = reviewService.createReview("product-uuid", request, user);

        assertThat(result.getRating()).isEqualTo(4);
        verify(reviewRepository).save(any());
    }

    @Test
    void createReview_whenProductNotFound_shouldThrow() {
        when(productRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.createReview("nonexistent", new ReviewRequest(), user))
                .isInstanceOf(ProductNotFoundException.class);
    }

    // --- deleteReview ---

    @Test
    void deleteReview_shouldDelete() {
        when(reviewRepository.findByUuid("review-uuid")).thenReturn(Optional.of(review));

        reviewService.deleteReview("review-uuid");

        verify(reviewRepository).delete(review);
    }

    @Test
    void deleteReview_whenNotFound_shouldThrow() {
        when(reviewRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.deleteReview("nonexistent"))
                .isInstanceOf(ReviewNotFoundException.class);
    }

    // --- voteReview ---

    @Test
    void voteReview_newVote_shouldCreate() {
        when(reviewRepository.findByUuid("review-uuid")).thenReturn(Optional.of(review));
        when(reviewVoteRepository.findByReviewIdAndUserId(1L, 1L)).thenReturn(Optional.empty());
        when(reviewVoteRepository.countByReviewIdAndVoteType(1L, "like")).thenReturn(1L);
        when(reviewVoteRepository.countByReviewIdAndVoteType(1L, "dislike")).thenReturn(0L);

        VoteResponse result = reviewService.voteReview("review-uuid", "like", user);

        assertThat(result.getVoteType()).isEqualTo("like");
        assertThat(result.getLikeCount()).isEqualTo(1);
        verify(reviewVoteRepository).save(any());
    }

    @Test
    void voteReview_changeVote_shouldUpdate() {
        ReviewVote existingVote = ReviewVote.builder().id(1L).review(review).user(user).voteType("dislike").build();

        when(reviewRepository.findByUuid("review-uuid")).thenReturn(Optional.of(review));
        when(reviewVoteRepository.findByReviewIdAndUserId(1L, 1L)).thenReturn(Optional.of(existingVote));
        when(reviewVoteRepository.countByReviewIdAndVoteType(1L, "like")).thenReturn(1L);
        when(reviewVoteRepository.countByReviewIdAndVoteType(1L, "dislike")).thenReturn(0L);

        VoteResponse result = reviewService.voteReview("review-uuid", "like", user);

        assertThat(result.getVoteType()).isEqualTo("like");
        verify(reviewVoteRepository).save(existingVote);
    }

    @Test
    void voteReview_removeVote_shouldDelete() {
        ReviewVote existingVote = ReviewVote.builder().id(1L).review(review).user(user).voteType("like").build();

        when(reviewRepository.findByUuid("review-uuid")).thenReturn(Optional.of(review));
        when(reviewVoteRepository.findByReviewIdAndUserId(1L, 1L)).thenReturn(Optional.of(existingVote));
        when(reviewVoteRepository.countByReviewIdAndVoteType(1L, "like")).thenReturn(0L);
        when(reviewVoteRepository.countByReviewIdAndVoteType(1L, "dislike")).thenReturn(0L);

        VoteResponse result = reviewService.voteReview("review-uuid", "like", user);

        assertThat(result.getVoteType()).isNull();
        verify(reviewVoteRepository).delete(existingVote);
    }

    @Test
    void voteReview_whenReviewNotFound_shouldThrow() {
        when(reviewRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.voteReview("nonexistent", "like", user))
                .isInstanceOf(ReviewNotFoundException.class);
    }
}
