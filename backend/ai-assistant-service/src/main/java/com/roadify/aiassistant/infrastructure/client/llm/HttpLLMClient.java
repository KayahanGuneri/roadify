package com.roadify.aiassistant.infrastructure.client.llm;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.roadify.aiassistant.domain.llm.LLMClient;
import com.roadify.aiassistant.domain.llm.LLMClientException;
import com.roadify.aiassistant.domain.llm.LLMRequest;
import com.roadify.aiassistant.domain.llm.LLMResponse;
import com.roadify.aiassistant.domain.llm.LLMTimeoutException;
import com.roadify.aiassistant.infrastructure.config.LLMProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.net.SocketTimeoutException;
import java.util.List;

/**
 * Türkçe Özet:
 * Ollama'nın native /api/chat endpoint'ine HTTP çağrısı yapan LLMClient implementasyonu.
 */
@Slf4j
public class HttpLLMClient implements LLMClient {

    private final RestClient restClient;
    private final LLMProperties llmProperties;

    public HttpLLMClient(RestClient restClient, LLMProperties llmProperties) {
        this.restClient = restClient;
        this.llmProperties = llmProperties;
    }

    @Override
    public LLMResponse chat(LLMRequest request) {
        String model = request.getModel() != null ? request.getModel() : llmProperties.getModel();

        OllamaChatRequest ollamaRequest = buildOllamaRequest(model, request);

        try {
            log.debug("Calling Ollama chat API with model={}", model);

            OllamaChatResponse response = restClient.post()
                    .uri("/api/chat")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(ollamaRequest)
                    .retrieve()
                    .body(OllamaChatResponse.class);

            if (response == null || response.message == null) {
                throw new LLMClientException("Ollama API returned empty response");
            }

            return LLMResponse.builder()
                    .answer(response.message.content)
                    .suggestions(List.of()) // Structured suggestions parsing can be added later
                    .model(response.model)
                    .promptTokens(response.promptEvalCount)
                    .completionTokens(response.evalCount)
                    .totalTokens(response.evalCount != null && response.promptEvalCount != null
                            ? response.evalCount + response.promptEvalCount
                            : null)
                    .build();

        } catch (RestClientResponseException ex) {
            // Non-2xx HTTP status codes
            log.error("Ollama API returned error status={}, body={}", ex.getRawStatusCode(), ex.getResponseBodyAsString());
            throw new LLMClientException(
                    "Ollama API error: HTTP " + ex.getRawStatusCode() + " - " + ex.getMessage(), ex
            );
        } catch (ResourceAccessException ex) {
            // I/O errors, including timeouts
            if (ex.getCause() instanceof SocketTimeoutException) {
                throw new LLMTimeoutException("Timeout while calling Ollama API", ex);
            }
            throw new LLMClientException("I/O error while calling Ollama API", ex);
        } catch (LLMClientException ex) {
            // Already a domain-specific exception, just propagate
            throw ex;
        } catch (Exception ex) {
            // Fallback for unexpected errors
            throw new LLMClientException("Unexpected error while calling Ollama API", ex);
        }
    }

    private OllamaChatRequest buildOllamaRequest(String model, LLMRequest request) {
        OllamaMessage systemMessage = null;
        if (request.getSystemPrompt() != null && !request.getSystemPrompt().isBlank()) {
            systemMessage = new OllamaMessage("system", request.getSystemPrompt());
        }

        OllamaMessage userMessage = new OllamaMessage("user", request.getPrompt());

        List<OllamaMessage> messages = systemMessage != null
                ? List.of(systemMessage, userMessage)
                : List.of(userMessage);

        Double temperature = request.getTemperature() != null
                ? request.getTemperature()
                : llmProperties.getTemperature();

        Integer maxTokens = request.getMaxTokens() != null
                ? request.getMaxTokens()
                : llmProperties.getMaxTokens();

        OllamaOptions options = new OllamaOptions(temperature, maxTokens);

        return new OllamaChatRequest(model, messages, false, options);
    }

    // ===== Ollama request/response DTOs (internal only) =====

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private record OllamaChatRequest(
            String model,
            List<OllamaMessage> messages,
            boolean stream,
            OllamaOptions options
    ) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private record OllamaMessage(
            String role,
            String content
    ) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private record OllamaOptions(
            @JsonProperty("temperature") Double temperature,
            @JsonProperty("num_predict") Integer numPredict
    ) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private record OllamaChatResponse(
            String model,
            OllamaMessage message,
            Boolean done,
            @JsonProperty("total_duration") Long totalDuration,
            @JsonProperty("prompt_eval_count") Integer promptEvalCount,
            @JsonProperty("eval_count") Integer evalCount
    ) {
    }
}
