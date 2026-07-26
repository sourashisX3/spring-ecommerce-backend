package com.example.ecommerce_backend.modules.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CreateRoomRequest(@NotBlank @Schema(description = "Topic of the chat room", example = "Order inquiry", requiredMode = Schema.RequiredMode.REQUIRED) String topic) {
}
