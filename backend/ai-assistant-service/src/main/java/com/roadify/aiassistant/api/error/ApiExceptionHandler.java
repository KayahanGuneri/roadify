package com.roadify.aiassistant.api.error;

import com.roadify.aiassistant.domain.llm.LLMClientException;
import com.roadify.aiassistant.domain.llm.LLMTimeoutException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.UUID;

/**
 * Türkçe Özet:
 * ai-assistant-service içindeki tüm controller'lar için
 * global exception handler.
 *
 * LLM ile ilgili özel exception'ları ve genel beklenmeyen hataları
 * tek bir error response formatına map eder.
 */
@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    private static final String TRACE_ID_KEY = "traceId";

    /**
     * LLM sağlayıcısı timeout olduğunda (örneğin Ollama zamanında cevap vermezse).
     */
    @ExceptionHandler(LLMTimeoutException.class)
    @ResponseStatus(HttpStatus.GATEWAY_TIMEOUT) // 504
    public ErrorResponseDTO handleLlmTimeout(LLMTimeoutException ex, HttpServletRequest request) {
        String traceId = resolveTraceId();
        log.error("LLM timeout while processing request. traceId={}, path={}", traceId, request.getRequestURI(), ex);

        return ErrorResponseDTO.builder()
                .errorCode("AI_LLM_TIMEOUT")
                .message("LLM provider did not respond in time.")
                .details("Path: " + request.getRequestURI())
                .traceId(traceId)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * LLM ile ilgili diğer client hataları (HTTP 4xx/5xx, I/O hataları vb.).
     */
    @ExceptionHandler(LLMClientException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY) // 502
    public ErrorResponseDTO handleLlmClientError(LLMClientException ex, HttpServletRequest request) {
        String traceId = resolveTraceId();
        log.error("LLM client error while processing request. traceId={}, path={}", traceId, request.getRequestURI(), ex);

        return ErrorResponseDTO.builder()
                .errorCode("AI_LLM_ERROR")
                .message("An error occurred while calling the LLM provider.")
                .details("Path: " + request.getRequestURI())
                .traceId(traceId)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * Diğer tüm beklenmeyen hatalar için fallback handler.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500
    public ErrorResponseDTO handleGenericError(Exception ex, HttpServletRequest request) {
        String traceId = resolveTraceId();
        log.error("Unexpected error while processing request. traceId={}, path={}", traceId, request.getRequestURI(), ex);

        return ErrorResponseDTO.builder()
                .errorCode("AI_INTERNAL_ERROR")
                .message("An unexpected error occurred while processing the request.")
                .details("Path: " + request.getRequestURI())
                .traceId(traceId)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * TraceId çözümleme:
     * - Eğer MDC'de traceId varsa onu kullan
     * - Yoksa yeni bir UUID üret ve MDC'ye koy
     */
    private String resolveTraceId() {
        String existing = MDC.get(TRACE_ID_KEY);
        if (existing != null && !existing.isBlank()) {
            return existing;
        }
        String generated = UUID.randomUUID().toString();
        MDC.put(TRACE_ID_KEY, generated);
        return generated;
    }
}
