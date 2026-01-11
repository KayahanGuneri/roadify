package com.roadify.aiassistant.domain.llm;

/**
 * Türkçe Özet:
 * LLM çağrısı belirtilen sürede cevap vermezse fırlatılan exception tipi.
 */
public class LLMTimeoutException extends LLMClientException {

    public LLMTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
