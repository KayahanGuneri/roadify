package com.roadify.analytics.domain.repository;

import com.roadify.analytics.domain.model.AiUsageStat;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for ai_usage_stats aggregate table.
 *
 * Türkçe Özet:
 * AI kullanım istatistikleri için repository arayüzü.
 */
public interface AiUsageStatsRepository {

    void incrementRequestCount(LocalDate date);

    void incrementAcceptedCount(LocalDate date);

    List<AiUsageStat> findBetween(LocalDate from, LocalDate to);
}
