package com.roadify.aiassistant.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Filters that will be used both in places-service request
 * and as context for the LLM.
 *
 * Note: No rating or price info for now.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AISuggestionFiltersDTO {

    /**
     * Optional category name.
     * Example: "RESTAURANT", "FUEL", "CAFE"...
     * (Matches places-service PlaceCategory enum values as String)
     */
    private String category;

    /**
     * Maximum acceptable detour in kilometers.
     */
    private Double maxDetourKm;

    /**
     * Optional paging fields.
     */
    private Integer limit;
    private Integer offset;
}
