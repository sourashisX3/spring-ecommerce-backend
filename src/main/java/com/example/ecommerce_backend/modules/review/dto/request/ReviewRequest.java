package com.example.ecommerce_backend.modules.review.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request object for creating a review")
public class ReviewRequest {

    @Schema(description = "Rating score (1-5)", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;

    @Schema(description = "Review title", example = "Great product!")
    private String title;

    @Schema(description = "Review comment", example = "I absolutely love this product!")
    private String comment;
}
