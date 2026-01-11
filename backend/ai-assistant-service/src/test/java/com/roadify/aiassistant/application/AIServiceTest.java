package com.roadify.aiassistant.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roadify.aiassistant.api.dto.AIChatRequestDTO;
import com.roadify.aiassistant.api.dto.AIChatResponseDTO;
import com.roadify.aiassistant.api.dto.AISuggestionFiltersDTO;
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
import com.roadify.aiassistant.infrastructure.config.LLMProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AIServiceTest {

    @Mock
    private RouteServiceClient routeServiceClient;

    @Mock
    private PlacesServiceClient placesServiceClient;

    @Mock
    private LLMClient llmClient;

    private AIService aiService;

    @BeforeEach
    void setUp() {
        // TripSuggestionPromptBuilder için basit config
        LLMProperties props = new LLMProperties();
        props.setModel("llama3");
        props.setMaxTokens(256);
        props.setTemperature(0.2);

        TripSuggestionPromptBuilder promptBuilder = new TripSuggestionPromptBuilder(props);
        LLMResponseMapper responseMapper = new LLMResponseMapper();
        ObjectMapper objectMapper = new ObjectMapper();

        aiService = new AIService(
                routeServiceClient,
                placesServiceClient,
                llmClient,
                promptBuilder,
                responseMapper,
                objectMapper
        );
    }

    @Test
    void chat_shouldCallRoutePlacesAndLlmAndReturnMappedResponse() {
        // Arrange
        String userId = "test-user";
        String routeId = "route-123";

        AISuggestionFiltersDTO filters = AISuggestionFiltersDTO.builder()
                .category("CAFE")
                .maxDetourKm(5.0)
                .limit(10)
                .offset(0)
                .build();

        AIChatRequestDTO request = new AIChatRequestDTO();
        request.setRouteId(routeId);
        request.setMessage("Kahve ve yemek molaları öner.");
        request.setFilters(filters);

        RouteDetailsDTO route = RouteDetailsDTO.builder()
                .id(routeId)
                .distanceKm(700.0)
                .durationMinutes(600.0)
                .fromLat(36.8867)
                .fromLng(30.7041)
                .toLat(41.0151)
                .toLng(28.9795)
                .build();

        PlaceDetailsDTO place = PlaceDetailsDTO.builder()
                .id("place-1")
                .name("Test Cafe")
                .category("CAFE")
                .detourKm(3.0)
                .build();

        when(routeServiceClient.getRouteById(routeId)).thenReturn(route);
        when(placesServiceClient.getPlacesForRoute(routeId, filters)).thenReturn(List.of(place));

        LLMResponse llmResponse = LLMResponse.builder()
                .answer("Here are some suggestions...")
                .suggestions(List.of()) // şimdilik boş
                .model("llama3")
                .build();

        ArgumentCaptor<LLMRequest> llmRequestCaptor = ArgumentCaptor.forClass(LLMRequest.class);
        when(llmClient.chat(llmRequestCaptor.capture())).thenReturn(llmResponse);

        // Act
        AIChatResponseDTO response = aiService.chat(userId, request);

        // Assert - LLM çağrısı yapılmış mı
        verify(routeServiceClient).getRouteById(routeId);
        verify(placesServiceClient).getPlacesForRoute(routeId, filters);
        verify(llmClient).chat(any(LLMRequest.class));

        // Assert - Response mapping
        assertNotNull(response);
        assertEquals("Here are some suggestions...", response.getAnswer());

        // Assert - LLMRequest içeriğine hızlı bir bakış
        LLMRequest captured = llmRequestCaptor.getValue();
        assertEquals("llama3", captured.getModel());
        assertTrue(captured.getPrompt().contains("Test Cafe"));
        assertTrue(captured.getPrompt().contains("CAFE"));
    }
}
