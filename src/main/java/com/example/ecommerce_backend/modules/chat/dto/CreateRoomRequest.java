package com.example.ecommerce_backend.modules.chat.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateRoomRequest(@NotBlank String topic) {
}
