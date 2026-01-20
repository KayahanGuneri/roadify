package com.roadify.analytics.domain.repository;

import com.roadify.analytics.domain.model.PopularPlaceCategoryStat;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for popular_place_categories aggregate table.
 *
 * Türkçe Özet:
 * Popüler mekan kategorileri istatistikleri için repository arayüzü.
 */
public interface PopularPlaceCategoriesRepository {

    void incrementCategoryCount(LocalDate date, String category, int incrementBy);

    List<PopularPlaceCategoryStat> findTopCategories(LocalDate from, LocalDate to, int limit);
}
