package com.roadify.aiassistant.api.error;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

/**
 * Türkçe Özet:
 * API seviyesinde tüm hata cevapları için ortak response modeli.
 * errorCode, mesaj, detay ve traceId içerir.
 */
@Value
@Builder
public class ErrorResponseDTO {

    /**
     * Stable, machine-readable error code.
     * Örn: "AI_LLM_TIMEOUT", "AI_LLM_ERROR", "AI_INTERNAL_ERROR"
     */
    String errorCode;

    /**
     * İnsan tarafından okunabilir kısa açıklama.
     */
    String message;

    /**
     * Ek detaylar (örn: request path, ilave bilgi vs.).
     */
    String details;

    /**
     * Request bazlı trace/correlation id.
     * MDC ile doldurulabilir; yoksa handler içinde üretilir.
     */
    String traceId;

    /**
     * Hatanın oluştuğu an (UTC).
     */
    Instant timestamp;
}
