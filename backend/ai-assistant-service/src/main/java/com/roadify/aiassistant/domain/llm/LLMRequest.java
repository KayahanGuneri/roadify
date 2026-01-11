package com.roadify.aiassistant.domain.llm;

import lombok.Builder;
import lombok.Value;

import java.util.Map;

/**
 * Türkçe Özet:
 * LLM'e gönderilecek isteği temsil eden domain modeli.
 * Prompt, model, temperature vb. parametreleri içerir.
 */
@Value
@Builder
public class LLMRequest {

    // Final prompt text that will be sent to the LLM
    String prompt;

    // Optional system-level instructions (can be null)
    String systemPrompt;

    // Model name; if null, default from configuration will be used
    String model;

    // Max tokens for this request; may override default configuration
    Integer maxTokens;

    // Temperature for this request; may override default configuration
    Double temperature;

    // Optional metadata (e.g. routeId, userId) for logging/analytics
    Map<String, String> metadata;
}
