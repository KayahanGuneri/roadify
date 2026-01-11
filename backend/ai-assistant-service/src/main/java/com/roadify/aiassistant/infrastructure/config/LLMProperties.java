package com.roadify.aiassistant.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Türkçe Özet:
 * Ollama tabanlı LLM sağlayıcısı için konfigürasyon ayarlarını tutar.
 * (baseUrl, model, timeout, temperature, maxTokens vb.)
 */
@Data
@ConfigurationProperties(prefix = "roadify.ai.llm")
public class LLMProperties {

    // Provider identifier, e.g. "ollama"
    private String provider;

    // Base URL of the Ollama server, e.g. http://localhost:11434
    private String baseUrl;

    // Not used for Ollama, but kept for future providers (e.g. OpenAI)
    private String apiKey;

    // Default model name, e.g. "llama3"
    private String model;

    // Request timeout for LLM calls
    private Duration timeout = Duration.ofSeconds(30);

    // Max tokens / prediction length
    private Integer maxTokens = 512;

    // Temperature for sampling
    private Double temperature = 0.3;
}
