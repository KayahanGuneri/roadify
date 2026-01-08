package com.roadify.aiassistant.infrastructure.client.llm;

import com.roadify.aiassistant.api.dto.AIChatResponseDTO;
import com.roadify.aiassistant.api.dto.SuggestionDTO;
import com.roadify.aiassistant.domain.llm.LLMClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * HTTP-based implementation of LLMClient.
 *
 * Şu an için dummy implementasyon çalışıyor; ileride Ollama / başka bir LLM'e
 * gerçek HTTP çağrısı ekleyeceğiz.
 */
@Service
@RequiredArgsConstructor
public class HttpLLMClient implements LLMClient {

    private final RestTemplate httpClient;

    @Value("${roadify.llm.base-url:http://localhost:11434}")
    private String baseUrl;

    @Value("${roadify.llm.chat-path:/api/chat}")
    private String chatPath;

    @Override
    public AIChatResponseDTO generateSuggestions(String contextJson, String userMessage) {
        // Gerçek LLM çağrısı için örnek request body (şimdilik kullanılmıyor)
        Map<String, Object> requestBody = Map.of(
                "model", "roadify-trip-assistant",
                "messages", List.of(
                        Map.of(
                                "role", "system",
                                "content", "You are an AI assistant helping users plan smarter road trips."
                        ),
                        Map.of(
                                "role", "user",
                                "content", buildPrompt(contextJson, userMessage)
                        )
                )
        );

        // TODO: LLM ayağa kalktığında gerçek HTTP çağrısını buraya koyacağız.
        // Örnek:
        // var response = httpClient.postForObject(
        //         baseUrl + chatPath,
        //         requestBody,
        //         OllamaChatResponse.class
        // );
        // Sonra OllamaChatResponse -> AIChatResponseDTO map edilecek.

        // Şimdilik dummy cevap döndürüyoruz:
        SuggestionDTO suggestion = SuggestionDTO.builder()
                .placeId("dummy-place-id")
                .name("Dummy Place (LLM not wired yet)")
                .shortReason("This is a placeholder suggestion until LLM is integrated.")
                .build();

        return AIChatResponseDTO.builder()
                .answer("LLM is not fully integrated yet; this is a dummy response.")
                .suggestions(List.of(suggestion))
                .build();
    }

    private String buildPrompt(String contextJson, String userMessage) {
        return """
                You are an AI assistant for a road trip planning app called Roadify.
                You receive:
                - a JSON context describing the current route, candidate places along the route and filters
                - a user message with additional preferences

                Your job:
                - Choose the best places for the user
                - Return a short, structured summary

                JSON context:
                %s

                User message:
                %s
                """.formatted(contextJson, userMessage);
    }
}
