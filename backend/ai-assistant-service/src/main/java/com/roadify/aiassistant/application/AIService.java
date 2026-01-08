package com.roadify.aiassistant.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roadify.aiassistant.api.dto.AIChatRequestDTO;
import com.roadify.aiassistant.api.dto.AIChatResponseDTO;
import com.roadify.aiassistant.domain.llm.LLMClient;
import com.roadify.aiassistant.application.client.PlacesServiceClient;
import com.roadify.aiassistant.application.client.RouteServiceClient;
import com.roadify.aiassistant.application.dto.PlaceDetailsDTO;
import com.roadify.aiassistant.application.dto.RouteDetailsDTO;
import com.roadify.aiassistant.application.model.AIContext;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Orchestrates calling route-service, places-service and LLM.
 */
@Service
@RequiredArgsConstructor
public class AIService {

    private final RouteServiceClient routeServiceClient;
    private final PlacesServiceClient placesServiceClient;
    private final LLMClient llmClient;
    private final ObjectMapper objectMapper;

    public AIChatResponseDTO chat(String userId, AIChatRequestDTO request) {

        // 1. Route details
        RouteDetailsDTO route = routeServiceClient.getRouteById(request.getRouteId());

        // 2. Places for route (filtered)
        List<PlaceDetailsDTO> places = placesServiceClient.getPlacesForRoute(
                request.getRouteId(),
                request.getFilters()
        );

        // 3. Build context
        AIContext context = AIContext.builder()
                .route(route)
                .places(places)
                .filters(request.getFilters())
                .userId(userId)
                .build();

        String contextJson = serializeContext(context);

        // 4. Ask LLM
        return llmClient.generateSuggestions(contextJson, request.getMessage());
    }

    private String serializeContext(AIContext context) {
        try {
            return objectMapper.writeValueAsString(context);
        } catch (JsonProcessingException e) {
            // In a real system, define a custom exception type + handler
            throw new IllegalStateException("Failed to serialize AI context", e);
        }
    }
}
