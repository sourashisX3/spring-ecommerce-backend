package com.example.ecommerce_backend.modules.review.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object for review data")
public class ReviewResponse {

    @Schema(description = "Review ID", example = "1")
    private Long id;
    @Schema(description = "Review UUID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String uuid;
    @Schema(description = "Product UUID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String productUuid;
    @Schema(description = "Product name", example = "Scented Candle")
    private String productName;
    @Schema(description = "Rating score", example = "5")
    private int rating;
    @Schema(description = "Review title", example = "Great product!")
    private String title;
    @Schema(description = "Review comment", example = "I love this product")
    private String comment;
    private boolean isActive;
    private boolean isVerifiedPurchase;
    @Schema(description = "First name of the reviewer", example = "John")
    private String userFirstName;
    @Schema(description = "Last name of the reviewer", example = "Doe")
    private String userLastName;
    @Schema(description = "Number of likes", example = "10")
    private long likeCount;
    @Schema(description = "Number of dislikes", example = "2")
    private long dislikeCount;
    @Schema(description = "Current user's vote (LIKE, DISLIKE, or null)", example = "LIKE")
    private String currentUserVote;
    @Schema(description = "Creation timestamp")
    private Instant createdAt;
    @Schema(description = "Last update timestamp")
    private Instant updatedAt;

    @JsonProperty("isActive")
    @Schema(description = "Whether the review is active", example = "true")
    public boolean isActive() {
        return isActive;
    }

    @JsonProperty("isActive")
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    @JsonProperty("isVerifiedPurchase")
    @Schema(description = "Whether the purchase is verified", example = "true")
    public boolean isVerifiedPurchase() {
        return isVerifiedPurchase;
    }

    @JsonProperty("isVerifiedPurchase")
    public void setVerifiedPurchase(boolean isVerifiedPurchase) {
        this.isVerifiedPurchase = isVerifiedPurchase;
    }
}
