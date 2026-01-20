package com.roadify.analytics.domain.repository;

import com.roadify.analytics.domain.model.DailyRouteUsage;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for daily_route_usage aggregate table.
 *
 * Türkçe Özet:
 * Günlük rota kullanımı istatistikleri için repository arayüzü.
 */
public interface DailyRouteUsageRepository {

    void incrementRouteCount(LocalDate date);

    List<DailyRouteUsage> findBetween(LocalDate from, LocalDate to);
}
