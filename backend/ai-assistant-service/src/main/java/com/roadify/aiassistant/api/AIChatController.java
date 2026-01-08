package com.roadify.aiassistant.api;

import com.roadify.aiassistant.api.dto.AIChatRequestDTO;
import com.roadify.aiassistant.api.dto.AIChatResponseDTO;
import com.roadify.aiassistant.application.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Public API for AI trip assistant.
 */
@RestController
@RequestMapping("/v1/ai/chat")
@RequiredArgsConstructor
public class AIChatController {

    private final AIService aiService;

    /**
     * AI chat endpoint.
     *
     * Expects caller (gateway) to forward user id in X-User-Id header.
     */
    @PostMapping
    public AIChatResponseDTO chat(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody AIChatRequestDTO request
    ) {
        return aiService.chat(userId, request);
    }
}
