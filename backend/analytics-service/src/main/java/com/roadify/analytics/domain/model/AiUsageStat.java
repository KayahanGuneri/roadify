package com.roadify.analytics.domain.model;

import java.time.LocalDate;

/**
 * Domain model for AI usage statistics per day.
 *
 * Türkçe Özet:
 * Gün bazında AI istek ve kabul sayılarını temsil eder.
 */
public class AiUsageStat {

    private final LocalDate date;
    private final int requestCount;
    private final int acceptedCount;

    public AiUsageStat(LocalDate date, int requestCount, int acceptedCount) {
        this.date = date;
        this.requestCount = requestCount;
        this.acceptedCount = acceptedCount;
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
        if (requestCount == 0) {
            return 0.0;
        }
        return (double) acceptedCount / requestCount;
    }

    public AiUsageStat withCounts(int newRequestCount, int newAcceptedCount) {
        return new AiUsageStat(this.date, newRequestCount, newAcceptedCount);
    }
}
