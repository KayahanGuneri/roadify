package com.roadify.aiassistant.application.llm;

import com.roadify.aiassistant.api.dto.AIChatResponseDTO;
import com.roadify.aiassistant.api.dto.SuggestionDTO;
import com.roadify.aiassistant.domain.llm.AISuggestion;
import com.roadify.aiassistant.domain.llm.LLMResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Türkçe Özet:
 * Domain seviyesindeki LLMResponse nesnesini API seviyesindeki
 * AIChatResponseDTO nesnesine dönüştüren mapper sınıfı.
 */
@Component
public class LLMResponseMapper {

    public AIChatResponseDTO toDto(LLMResponse response) {
        AIChatResponseDTO dto = new AIChatResponseDTO();
        dto.setAnswer(response.getAnswer());
        dto.setSuggestions(mapSuggestions(response.getSuggestions()));
        // Eğer AIChatResponseDTO içinde model / usage gibi alanlar varsa,
        // bunları da burada set edebilirsin.
        return dto;
    }

    private List<SuggestionDTO> mapSuggestions(List<AISuggestion> suggestions) {
        if (suggestions == null || suggestions.isEmpty()) {
            return List.of();
        }

        return suggestions.stream()
                .map(this::mapSuggestion)
                .collect(Collectors.toList());
    }

    private SuggestionDTO mapSuggestion(AISuggestion suggestion) {
        // SuggestionDTO is immutable (@Value + @Builder), so we must use the builder.
        return SuggestionDTO.builder()
                .placeId(suggestion.getPlaceId())
                // We map domain "title" to API "name"
                .name(suggestion.getTitle())
                // We map domain "description" to API "shortReason"
                .shortReason(suggestion.getDescription())
                .build();
    }
}
