package com.roadify.analytics.api.dto;

import java.time.LocalDate;

/**
 * DTO for popular categories analytics.
 *
 * Türkçe Özet:
 * Popüler mekan kategorilerini döndürmek için kullanılan DTO.
 */
public class PopularCategoryDTO {

    private final LocalDate date;
    private final String category;
    private final int count;

    public PopularCategoryDTO(LocalDate date, String category, int count) {
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
}
