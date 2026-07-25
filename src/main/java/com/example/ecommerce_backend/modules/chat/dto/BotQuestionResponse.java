package com.example.ecommerce_backend.modules.chat.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BotQuestionResponse {
    private String questionKey;
    private String questionText;
    private String options;
    private String botResponse;
    private boolean isEscalationPoint;
}
