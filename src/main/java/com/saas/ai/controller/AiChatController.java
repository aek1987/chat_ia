package com.saas.ai.controller;

import com.saas.ai.dto.Dtos.*;
import com.saas.ai.service.AiChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiChatController {

    private final AiChatService aiChatService;

    /**
     * POST /api/ai/chat
     * Body: { "message": "Où est ma commande 1 ?", "customerId": 123 }
     */
    @PostMapping("/chat")
    public ResponseEntity<ApiResponse<ChatResponse>> chat(@RequestBody ChatRequest request) {
        if (request.getMessage() == null || request.getMessage().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Le message ne peut pas être vide."));
        }
        ChatResponse response = aiChatService.processMessage(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
