package com.roadify.analytics.api.dto;

import java.time.LocalDate;

/**
 * DTO for overview analytics.
 *
 * Türkçe Özet:
 * Genel dashboard özet verilerini döndürmek için kullanılan DTO.
 */
public class AnalyticsOverviewDTO {

    private final LocalDate fromDate;
    private final LocalDate toDate;
    private final int totalRoutes;
    private final int totalAiRequests;
    private final int totalAiAccepted;

    public AnalyticsOverviewDTO(
            LocalDate fromDate,
            LocalDate toDate,
            int totalRoutes,
            int totalAiRequests,
            int totalAiAccepted
    ) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.totalRoutes = totalRoutes;
        this.totalAiRequests = totalAiRequests;
        this.totalAiAccepted = totalAiAccepted;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public int getTotalRoutes() {
        return totalRoutes;
    }

    public int getTotalAiRequests() {
        return totalAiRequests;
    }

    public int getTotalAiAccepted() {
        return totalAiAccepted;
    }
}
