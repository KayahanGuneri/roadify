package com.roadify.analytics.api.dto;

import java.time.LocalDate;

/**
 * DTO for AI usage analytics.
 *
 * Türkçe Özet:
 * AI kullanım istatistiklerini döndürmek için kullanılan DTO.
 */
public class AiUsageDTO {

    private final LocalDate date;
    private final int requestCount;
    private final int acceptedCount;
    private final double acceptanceRate;

    public AiUsageDTO(LocalDate date, int requestCount, int acceptedCount, double acceptanceRate) {
        this.date = date;
        this.requestCount = requestCount;
        this.acceptedCount = acceptedCount;
        this.acceptanceRate = acceptanceRate;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public int getAcceptedCount() {
        return acceptedCount;
    }

    public double getAcceptanceRate() {
        return acceptanceRate;
    }
}
