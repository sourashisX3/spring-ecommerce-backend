package com.example.ecommerce_backend.modules.chat.controller;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.core.dto.StatusRequest;
import com.example.ecommerce_backend.modules.chat.service.ChatBotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat-questions")
@Tag(name = "Chat Bot Question", description = "Chat bot question management APIs")
public class ChatBotQuestionController {

    @Autowired
    private ChatBotService chatBotService;

    @PatchMapping("/{uuid}/status")
    @RequiresPermission("chatbot:write")
    @Operation(summary = "Toggle chat question status", description = "Activates or deactivates a chat bot question")
    public ResponseEntity<ApiResponse<Void>> toggleStatus(
            @PathVariable String uuid,
            @Valid @RequestBody StatusRequest request
    ) {
        boolean changed = chatBotService.toggleStatus(uuid, request.isActive());
        String message = changed ? "Chat question status updated successfully" : "Chat question is already " + (request.isActive() ? "active" : "inactive");
        return ApiResponse.success(null, message);
    }
}
