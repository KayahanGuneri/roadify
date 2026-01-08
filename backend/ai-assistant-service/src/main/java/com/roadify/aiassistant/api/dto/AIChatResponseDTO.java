package com.roadify.aiassistant.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response returned from AI assistant.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIChatResponseDTO {

    /**
     * Natural language answer to show in UI.
     */
    private String answer;

    /**
     * Structured suggestions that UI can render as cards.
     */
    private List<SuggestionDTO> suggestions;
}
