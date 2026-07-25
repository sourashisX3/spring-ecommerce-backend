package com.example.ecommerce_backend.modules.review.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {

    private Long id;
    private String uuid;
    private int rating;
    private String title;
    private String comment;
    private boolean isActive;
    private boolean isVerifiedPurchase;
    private String userFirstName;
    private String userLastName;
    private long likeCount;
    private long dislikeCount;
    private String currentUserVote;
    private Instant createdAt;
    private Instant updatedAt;

    @JsonProperty("isActive")
    public boolean isActive() {
        return isActive;
    }

    @JsonProperty("isActive")
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    @JsonProperty("isVerifiedPurchase")
    public boolean isVerifiedPurchase() {
        return isVerifiedPurchase;
    }

    @JsonProperty("isVerifiedPurchase")
    public void setVerifiedPurchase(boolean isVerifiedPurchase) {
        this.isVerifiedPurchase = isVerifiedPurchase;
    }
}
