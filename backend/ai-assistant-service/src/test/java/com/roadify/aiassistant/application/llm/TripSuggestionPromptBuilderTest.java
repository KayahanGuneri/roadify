package com.roadify.aiassistant.application.llm;

import com.roadify.aiassistant.api.dto.AISuggestionFiltersDTO;
import com.roadify.aiassistant.application.dto.PlaceDetailsDTO;
import com.roadify.aiassistant.application.dto.RouteDetailsDTO;
import com.roadify.aiassistant.application.model.AIContext;
import com.roadify.aiassistant.domain.llm.LLMRequest;
import com.roadify.aiassistant.infrastructure.config.LLMProperties;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TripSuggestionPromptBuilderTest {

    @Test
    void buildRequest_shouldIncludeRouteFiltersAndUserInPromptAndMetadata() {
        // Arrange
        LLMProperties props = new LLMProperties();
        props.setModel("llama3");
        props.setMaxTokens(256);
        props.setTemperature(0.3);

        TripSuggestionPromptBuilder builder = new TripSuggestionPromptBuilder(props);

        RouteDetailsDTO route = RouteDetailsDTO.builder()
                .id("route-123")
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
                .detourKm(3.5)
                .build();

        AISuggestionFiltersDTO filters = AISuggestionFiltersDTO.builder()
                .category("CAFE")
                .maxDetourKm(5.0)
                .limit(10)
                .offset(0)
                .build();

        AIContext context = AIContext.builder()
                .route(route)
                .places(List.of(place))
                .filters(filters)
                .userId("test-user")
                .build();

        String userMessage = "Antalya'dan İstanbul'a giderken kahve molası öner.";

        // Act
        LLMRequest request = builder.buildRequest(context, userMessage);

        // Assert - config değerleri
        assertEquals("llama3", request.getModel());
        assertEquals(256, request.getMaxTokens());
        assertEquals(0.3, request.getTemperature());

        // Assert - metadata
        assertEquals("route-123", request.getMetadata().get("routeId"));
        assertEquals("test-user", request.getMetadata().get("userId"));

        // Assert - prompt içeriği
        String prompt = request.getPrompt();
        assertTrue(prompt.contains("Antalya"), "Prompt should contain user message / route context");
        assertTrue(prompt.contains("route-123") || prompt.contains("700.0"),
                "Prompt should contain some route details");
        assertTrue(prompt.contains("Test Cafe"), "Prompt should contain place name");
        assertTrue(prompt.contains("CAFE"), "Prompt should contain place category");
        assertTrue(prompt.contains("max detour") || prompt.contains("Maximum acceptable detour")
                        || prompt.contains("5.0"),
                "Prompt should mention filters / max detour");
    }
}
