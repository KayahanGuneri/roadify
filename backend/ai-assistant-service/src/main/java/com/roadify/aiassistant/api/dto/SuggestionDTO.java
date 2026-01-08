package com.roadify.aiassistant.api.dto;

import lombok.Builder;
import lombok.Value;

/**
 * Single suggestion returned by the AI assistant.
 */
@Value
@Builder
public class SuggestionDTO {

    /**
     * ID of the place in places-service.
     */
    String placeId;

    /**
     * Human-readable place name.
     */
    String name;

    /**
     * Short explanation why this place is recommended.
     */
    String shortReason;
}
