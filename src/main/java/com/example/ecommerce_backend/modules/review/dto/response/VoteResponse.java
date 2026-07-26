package com.example.ecommerce_backend.modules.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object for vote data")
public class VoteResponse {

    @Schema(description = "Type of vote", example = "LIKE")
    private String voteType;
    @Schema(description = "Updated like count", example = "11")
    private long likeCount;
    @Schema(description = "Updated dislike count", example = "2")
    private long dislikeCount;
}
