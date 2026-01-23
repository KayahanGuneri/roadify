// src/types/analytics.ts

/**
 * English:
 * DTOs for analytics endpoints returned by analytics-service (via gateway-bff).
 *
 * Türkçe Özet:
 * Analytics endpointlerinden dönen DTO tipleri (curl çıktısına birebir uyumlu).
 */

export type AnalyticsOverviewDTO = {
    fromDate: string;       // "2026-01-01"
    toDate: string;         // "2026-01-23"
    totalRoutes: number;    // 2
    totalAiRequests: number; // 1
    totalAiAccepted: number; // 1
};

export type PopularCategoryDTO = {
    date: string;      // "2026-01-22"
    category: string;  // "CAFE"
    count: number;     // 2
};

export type AiUsageDTO = {
    date: string;           // "2026-01-22"
    requestCount: number;   // 1
    acceptedCount: number;  // 1
    acceptanceRate: number; // 1.0
};
