package com.example.ecommerce_backend.modules.review.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteResponse {

    private String voteType;
    private long likeCount;
    private long dislikeCount;
}
