package com.roadify.aiassistant.api.error;

import com.roadify.aiassistant.domain.llm.LLMClientException;
import com.roadify.aiassistant.domain.llm.LLMTimeoutException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApiExceptionHandlerTest {

    @Test
    void handleLlmTimeout_shouldReturnProperErrorResponse() {
        ApiExceptionHandler handler = new ApiExceptionHandler();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/v1/ai/chat");

        LLMTimeoutException ex = new LLMTimeoutException("Timeout", null);

        ErrorResponseDTO dto = handler.handleLlmTimeout(ex, request);

        assertEquals("AI_LLM_TIMEOUT", dto.getErrorCode());
        assertEquals("LLM provider did not respond in time.", dto.getMessage());
        assertTrue(dto.getDetails().contains("/v1/ai/chat"));
        assertNotNull(dto.getTraceId());
        assertNotNull(dto.getTimestamp());
    }

    @Test
    void handleLlmClientError_shouldReturnProperErrorResponse() {
        ApiExceptionHandler handler = new ApiExceptionHandler();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/v1/ai/chat");

        LLMClientException ex = new LLMClientException("Client error");

        ErrorResponseDTO dto = handler.handleLlmClientError(ex, request);

        assertEquals("AI_LLM_ERROR", dto.getErrorCode());
        assertEquals("An error occurred while calling the LLM provider.", dto.getMessage());
        assertTrue(dto.getDetails().contains("/v1/ai/chat"));
        assertNotNull(dto.getTraceId());
        assertNotNull(dto.getTimestamp());
    }
}
