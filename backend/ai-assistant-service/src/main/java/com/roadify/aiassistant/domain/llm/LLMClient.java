package com.roadify.aiassistant.domain.llm;

import com.roadify.aiassistant.api.dto.AIChatResponseDTO;

/**
 * LLMClient
 *
 * English:
 * Abstraction for calling an external Large Language Model (LLM).
 * Implementations are responsible for transforming the JSON context
 * and user message into an HTTP request and mapping the HTTP response
 * back to AIChatResponseDTO.
 *
 * Türkçe Özet:
 * Harici LLM servisine erişim için kullanılan arayüz.
 * JSON context + kullanıcı mesajını HTTP isteğine çevirip
 * cevabı AIChatResponseDTO'ya mapleyen implementasyonlar sağlar.
 */
public interface LLMClient {



    LLMResponse chat(LLMRequest request);
}
