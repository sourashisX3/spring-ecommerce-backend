package com.example.ecommerce_backend.modules.review.controller;

import com.example.ecommerce_backend.core.aspect.AuthorizationAspect;
import com.example.ecommerce_backend.modules.review.dto.response.ReviewResponse;
import com.example.ecommerce_backend.modules.review.dto.response.VoteResponse;
import com.example.ecommerce_backend.modules.review.service.ReviewService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReviewService reviewService;

    @MockitoBean
    private AuthorizationAspect authorizationAspect;

    @BeforeEach
    void setUpAuth() throws Throwable {
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                ProceedingJoinPoint pjp = invocation.getArgument(0);
                try {
                    return pjp.proceed();
                } catch (RuntimeException e) {
                    throw e;
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
        }).when(authorizationAspect).checkPermission(any(), any());
    }

    @Test
    void getReviews_shouldReturnList() throws Exception {
        List<ReviewResponse> reviews = List.of(
                ReviewResponse.builder().uuid("uuid-1").rating(5).userFirstName("John").build()
        );

        when(reviewService.getReviews(anyString(), isNull())).thenReturn(reviews);

        mockMvc.perform(get("/products/product-uuid/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].rating").value(5));
    }

    @Test
    void getReviews_withPagination_shouldReturnPage() throws Exception {
        ReviewResponse review = ReviewResponse.builder().uuid("uuid-1").rating(4).userFirstName("Jane").build();
        org.springframework.data.domain.Page<ReviewResponse> page =
                new org.springframework.data.domain.PageImpl<>(List.of(review));

        when(reviewService.getReviews(anyString(), any(org.springframework.data.domain.Pageable.class), isNull()))
                .thenReturn(page);

        mockMvc.perform(get("/products/product-uuid/reviews?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].rating").value(4))
                .andExpect(jsonPath("$.pagination").exists());
    }

    @Test
    void createReview_shouldReturnCreated() throws Exception {
        ReviewResponse response = ReviewResponse.builder()
                .uuid("uuid-new").rating(5).userFirstName("John").build();

        when(reviewService.createReview(anyString(), any(), any())).thenReturn(response);

        mockMvc.perform(post("/products/product-uuid/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "rating": 5,
                                    "title": "Great!",
                                    "comment": "Love it"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.response.rating").value(5));
    }

    @Test
    void deleteReview_shouldReturnSuccess() throws Exception {
        mockMvc.perform(delete("/reviews/review-uuid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Review deleted successfully"));
    }

    @Test
    void voteReview_shouldReturnVoteResponse() throws Exception {
        VoteResponse voteResponse = VoteResponse.builder()
                .voteType("like").likeCount(5).dislikeCount(2).build();

        when(reviewService.voteReview(eq("review-uuid"), eq("like"), any())).thenReturn(voteResponse);

        mockMvc.perform(post("/reviews/review-uuid/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "voteType": "like"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.voteType").value("like"))
                .andExpect(jsonPath("$.response.likeCount").value(5));
    }
}
