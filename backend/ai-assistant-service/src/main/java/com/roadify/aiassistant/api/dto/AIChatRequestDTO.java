package com.roadify.aiassistant.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body for AI trip assistant chat.
 *
 * Used by mobile client to ask for suggestions on a given route.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIChatRequestDTO {

    /**
     * Route identifier (from route-service / trip-planner).
     */
    private String routeId;

    /**
     * Optional filters that will be used to narrow down places
     * before sending context to the LLM.
     */
    private AISuggestionFiltersDTO filters;

    /**
     * Free-form user message, e.g.
     * "I want nice cafes and restaurants for lunch on the way."
     */
    private String message;
}
