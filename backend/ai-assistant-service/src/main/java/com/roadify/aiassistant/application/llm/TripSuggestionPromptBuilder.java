package com.roadify.aiassistant.application.llm;

import com.roadify.aiassistant.application.model.AIContext;
import com.roadify.aiassistant.domain.llm.LLMRequest;
import com.roadify.aiassistant.infrastructure.config.LLMProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Türkçe Özet:
 * AIContext içindeki route, places, filtreler ve kullanıcı mesajını kullanarak
 * LLM'e gönderilecek prompt ve LLMRequest nesnesini oluşturan bileşen.
 *
 * Not:
 * - RouteDetailsDTO: id, distanceKm, durationMinutes, fromLat/fromLng, toLat/toLng
 * - AISuggestionFiltersDTO: category, maxDetourKm, limit, offset
 */
@Component
public class TripSuggestionPromptBuilder {

    private final LLMProperties llmProperties;

    public TripSuggestionPromptBuilder(LLMProperties llmProperties) {
        this.llmProperties = llmProperties;
    }

    /**
     * Build an LLMRequest using AIContext and the user's message.
     */
    public LLMRequest buildRequest(AIContext context, String userMessage) {
        String systemPrompt = buildSystemPrompt();
        String userPrompt = buildUserPrompt(context, userMessage);

        Map<String, String> metadata = new HashMap<>();
        if (context.getRoute() != null && context.getRoute().getId() != null) {
            metadata.put("routeId", context.getRoute().getId());
        }
        if (context.getUserId() != null) {
            metadata.put("userId", context.getUserId());
        }

        return LLMRequest.builder()
                .systemPrompt(systemPrompt)
                .prompt(userPrompt)
                .model(llmProperties.getModel())
                .maxTokens(llmProperties.getMaxTokens())
                .temperature(llmProperties.getTemperature())
                .metadata(metadata)
                .build();
    }

    /**
     * Build system-level instructions for the LLM.
     */
    private String buildSystemPrompt() {
        // System-level instructions for the LLM
        return """
                You are Roadify, an AI assistant specialized in planning road trips.
                Your job is to suggest optimal stops along the user's driving route:
                food, fuel, cafes, sightseeing, hotels, markets, WC, camping spots and more.
                Always explain briefly why each suggestion is relevant (distance, detour, category).
                Respond in a friendly but concise style.
                If filters are provided (e.g. category, max detour), respect them strictly.
                """;
    }

    /**
     * Build the user-facing prompt text using route, places and filters from AIContext.
     */
    private String buildUserPrompt(AIContext context, String userMessage) {
        StringBuilder sb = new StringBuilder();

        // User message
        sb.append("User message:\n");
        sb.append(userMessage).append("\n\n");

        // Route details
        sb.append("Route details:\n");
        if (context.getRoute() != null) {
            sb.append("Route ID: ").append(nullSafe(context.getRoute().getId())).append("\n");
            sb.append("From (lat,lng): ")
                    .append(context.getRoute().getFromLat())
                    .append(", ")
                    .append(context.getRoute().getFromLng())
                    .append("\n");
            sb.append("To (lat,lng): ")
                    .append(context.getRoute().getToLat())
                    .append(", ")
                    .append(context.getRoute().getToLng())
                    .append("\n");
            sb.append("Distance (km): ").append(context.getRoute().getDistanceKm()).append("\n");
            sb.append("Duration (minutes): ").append(context.getRoute().getDurationMinutes()).append("\n");
        } else {
            sb.append("No explicit route details available.\n");
        }
        sb.append("\n");

        // Filters
        sb.append("User filters / preferences:\n");
        if (context.getFilters() != null) {
            sb.append(describeFilters(context)).append("\n");
        } else {
            sb.append("No explicit filters were provided.\n");
        }
        sb.append("\n");

        // Candidate places
        sb.append("Candidate places along the route:\n");
        if (context.getPlaces() != null && !context.getPlaces().isEmpty()) {
            context.getPlaces().forEach(place -> {
                sb.append("- [")
                        .append(nullSafe(place.getCategory()))
                        .append("] ")
                        .append(nullSafe(place.getName()))
                        .append(" (detourKm=")
                        .append(place.getDetourKm())
                        .append(")\n");
            });
        } else {
            sb.append("No candidate places were provided.\n");
        }
        sb.append("\n");

        sb.append("Please suggest the best 3-7 stops for this user, ");
        sb.append("prioritizing minimal detour and relevance to the user message and filters.\n");

        return sb.toString();
    }

    /**
     * Describe filters in a human-readable way for the LLM.
     * AISuggestionFiltersDTO: category, maxDetourKm, limit, offset.
     */
    private String describeFilters(AIContext context) {
        StringBuilder sb = new StringBuilder();
        var filters = context.getFilters();

        boolean any = false;

        if (filters.getCategory() != null && !filters.getCategory().isBlank()) {
            sb.append("Preferred category: ").append(filters.getCategory()).append(". ");
            any = true;
        }
        if (filters.getMaxDetourKm() != null) {
            sb.append("Maximum acceptable detour: ")
                    .append(filters.getMaxDetourKm())
                    .append(" km. ");
            any = true;
        }
        if (filters.getLimit() != null) {
            sb.append("Result limit: ")
                    .append(filters.getLimit())
                    .append(". ");
            any = true;
        }
        if (filters.getOffset() != null) {
            sb.append("Result offset: ")
                    .append(filters.getOffset())
                    .append(". ");
            any = true;
        }

        if (!any) {
            sb.append("Filters object is present but no specific constraints are set.");
        }

        return sb.toString();
    }

    private String nullSafe(String value) {
        return value != null ? value : "N/A";
    }
}
