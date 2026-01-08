package com.roadify.aiassistant.application.model;

import com.roadify.aiassistant.api.dto.AISuggestionFiltersDTO;
import com.roadify.aiassistant.application.dto.PlaceDetailsDTO;
import com.roadify.aiassistant.application.dto.RouteDetailsDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Internal context object that will be serialized to JSON
 * and sent to the LLM.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIContext {

    private RouteDetailsDTO route;
    private List<PlaceDetailsDTO> places;
    private AISuggestionFiltersDTO filters;
    private String userId;
}
