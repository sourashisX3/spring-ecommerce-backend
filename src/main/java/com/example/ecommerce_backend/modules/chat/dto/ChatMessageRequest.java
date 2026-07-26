package com.example.ecommerce_backend.modules.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ChatMessageRequest(@NotBlank @Schema(description = "Content of the chat message", example = "Hello, I need help with my order", requiredMode = Schema.RequiredMode.REQUIRED) String content) {
}
