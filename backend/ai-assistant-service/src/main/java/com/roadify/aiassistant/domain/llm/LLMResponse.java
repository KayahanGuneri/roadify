package com.roadify.aiassistant.domain.llm;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * Türkçe Özet:
 * LLM'den gelen cevabı temsil eden domain modeli.
 */
@Value
@Builder
public class LLMResponse {

    // Raw text answer from the LLM
    String answer;

    // Optional structured suggestions parsed from the answer
    List<AISuggestion> suggestions;

    // Model actually used by the provider
    String model;

    // Token usage / evaluation stats if available
    Integer promptTokens;
    Integer completionTokens;
    Integer totalTokens;
}
