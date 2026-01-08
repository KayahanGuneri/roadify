package com.roadify.aiassistant.application.client;

import com.roadify.aiassistant.api.dto.AISuggestionFiltersDTO;
import com.roadify.aiassistant.application.dto.PlaceDetailsDTO;

import java.util.List;

/**
 * Abstraction over places-service HTTP API.
 */
public interface PlacesServiceClient {

    List<PlaceDetailsDTO> getPlacesForRoute(String routeId, AISuggestionFiltersDTO filters);
}
