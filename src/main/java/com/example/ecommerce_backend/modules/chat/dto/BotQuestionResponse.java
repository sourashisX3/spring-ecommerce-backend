package com.example.ecommerce_backend.modules.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Response containing a bot question with possible options")
public class BotQuestionResponse {
    @Schema(description = "Unique key identifying the question", example = "greeting")
    private String questionKey;
    @Schema(description = "The question text displayed to the user", example = "How can I help you today?")
    private String questionText;
    @Schema(description = "JSON string of available options for the question", example = "[\"Track Order\",\"Return Item\"]")
    private String options;
    @Schema(description = "Automated response from the bot", example = "I can help you track your order.")
    private String botResponse;
    @Schema(description = "Whether this question triggers escalation to a human agent", example = "false")
    private boolean isEscalationPoint;
}
