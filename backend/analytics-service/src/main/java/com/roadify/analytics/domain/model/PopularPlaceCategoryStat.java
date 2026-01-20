package com.roadify.analytics.domain.model;

import java.time.LocalDate;

/**
 * Domain model for popular place categories per day.
 *
 * Türkçe Özet:
 * Gün bazında popüler mekan kategorilerini ve sayısını temsil eder.
 */
public class PopularPlaceCategoryStat {

    private final LocalDate date;
    private final String category;
    private final int count;

    public PopularPlaceCategoryStat(LocalDate date, String category, int count) {
        this.date = date;
        this.category = category;
        this.count = count;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public int getCount() {
        return count;
    }

    public PopularPlaceCategoryStat withCount(int newCount) {
        return new PopularPlaceCategoryStat(this.date, this.category, newCount);
    }
}
