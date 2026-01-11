package com.roadify.aiassistant.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roadify.aiassistant.api.dto.AIChatRequestDTO;
import com.roadify.aiassistant.api.dto.AIChatResponseDTO;
import com.roadify.aiassistant.application.client.PlacesServiceClient;
import com.roadify.aiassistant.application.client.RouteServiceClient;
import com.roadify.aiassistant.application.dto.PlaceDetailsDTO;
import com.roadify.aiassistant.application.dto.RouteDetailsDTO;
import com.roadify.aiassistant.application.llm.LLMResponseMapper;
import com.roadify.aiassistant.application.llm.TripSuggestionPromptBuilder;
import com.roadify.aiassistant.application.model.AIContext;
import com.roadify.aiassistant.domain.llm.LLMClient;
import com.roadify.aiassistant.domain.llm.LLMRequest;
import com.roadify.aiassistant.domain.llm.LLMResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Orchestrates calling route-service, places-service and LLM.
 */
@Service
@RequiredArgsConstructor
public class AIService {

    private static final String ROUTE_ID_KEY = "routeId";

    private final RouteServiceClient routeServiceClient;
    private final PlacesServiceClient placesServiceClient;
    private final LLMClient llmClient;
    private final TripSuggestionPromptBuilder promptBuilder;
    private final LLMResponseMapper responseMapper;
    private final ObjectMapper objectMapper;

    /**
     * Main entry point for AI chat orchestration.
     */
    public AIChatResponseDTO chat(String userId, AIChatRequestDTO request) {

        String originalRouteIdInMdc = MDC.get(ROUTE_ID_KEY);
        try {
            // routeId'yi MDC'ye koy (loglarda gözüksün)
            if (request.getRouteId() != null && !request.getRouteId().isBlank()) {
                MDC.put(ROUTE_ID_KEY, request.getRouteId());
            }

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

            // 4. LLM request
            LLMRequest llmRequest = promptBuilder.buildRequest(context, request.getMessage());

            // 5. Call LLM
            LLMResponse llmResponse = llmClient.chat(llmRequest);

            // 6. Map to API DTO
            return responseMapper.toDto(llmResponse);

        } finally {

            if (originalRouteIdInMdc != null) {
                MDC.put(ROUTE_ID_KEY, originalRouteIdInMdc);
            } else {
                MDC.remove(ROUTE_ID_KEY);
            }
        }
    }

    private String serializeContext(AIContext context) {
        try {
            return objectMapper.writeValueAsString(context);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize AI context", e);
        }
    }
}
