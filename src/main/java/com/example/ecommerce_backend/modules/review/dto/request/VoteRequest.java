package com.example.ecommerce_backend.modules.review.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for voting on a review")
public class VoteRequest {

    @Schema(description = "Type of vote (LIKE or DISLIKE)", example = "LIKE", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String voteType;
}
