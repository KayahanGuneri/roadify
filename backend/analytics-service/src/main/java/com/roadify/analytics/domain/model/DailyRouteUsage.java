package com.roadify.analytics.domain.model;

import java.time.LocalDate;

/**
 * Domain model for daily route usage statistics.
 *
 * Türkçe Özet:
 * Gün bazlı oluşturulan rota sayısını temsil eder.
 */
public class DailyRouteUsage {

    private final LocalDate date;
    private final int routeCount;

    public DailyRouteUsage(LocalDate date, int routeCount) {
        this.date = date;
        this.routeCount = routeCount;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getRouteCount() {
        return routeCount;
    }

    public DailyRouteUsage withRouteCount(int newRouteCount) {
        return new DailyRouteUsage(this.date, newRouteCount);
    }
}
