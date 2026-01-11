package com.roadify.aiassistant.domain.llm;

import lombok.Builder;
import lombok.Value;

/**
 * Türkçe Özet:
 * LLM'in ürettiği tekil bir öneriyi temsil eden domain modeli.
 */
@Value
@Builder
public class AISuggestion {

    // Human-readable title for the suggestion
    String title;

    // Free-form description / reasoning text
    String description;

    // Optional placeId to correlate with domain objects
    String placeId;

    // Optional score / confidence
    Double score;
}
