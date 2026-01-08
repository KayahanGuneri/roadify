package com.roadify.aiassistant.domain.llm;

/**
 * LLMClientException
 *
 * English:
 * Runtime exception used when the LLM client fails to call the
 * underlying HTTP API or receives an invalid response.
 *
 * Türkçe Özet:
 * LLM HTTP çağrısı sırasında hata olduğunda fırlatılan
 * çalışma zamanı istisnası.
 */
public class LLMClientException extends RuntimeException {

    public LLMClientException(String message) {
        super(message);
    }

    public LLMClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
