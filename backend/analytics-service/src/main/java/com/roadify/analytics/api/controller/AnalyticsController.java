package com.roadify.analytics.api.controller;

import com.roadify.analytics.api.dto.AiUsageDTO;
import com.roadify.analytics.api.dto.AnalyticsOverviewDTO;
import com.roadify.analytics.api.dto.PopularCategoryDTO;
import com.roadify.analytics.application.service.AnalyticsReadService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for analytics endpoints.
 *
 * Türkçe Özet:
 * Analytics ile ilgili REST endpoint'lerini sağlayan controller.
 */
@RestController
@RequestMapping("/v1/analytics")
public class AnalyticsController {

    private final AnalyticsReadService analyticsReadService;

    public AnalyticsController(AnalyticsReadService analyticsReadService) {
        this.analyticsReadService = analyticsReadService;
    }

    @GetMapping("/overview")
    public AnalyticsOverviewDTO getOverview(
            @RequestParam("from")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @RequestParam("to")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to
    ) {
        return analyticsReadService.getOverview(from, to);
    }

    @GetMapping("/popular-categories")
    public List<PopularCategoryDTO> getPopularCategories(
            @RequestParam("from")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @RequestParam("to")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to,

            @RequestParam(name = "limit", defaultValue = "10")
            int limit
    ) {
        return analyticsReadService.getPopularCategories(from, to, limit);
    }

    @GetMapping("/ai-usage")
    public List<AiUsageDTO> getAiUsage(
            @RequestParam("from")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @RequestParam("to")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to
    ) {
        return analyticsReadService.getAiUsage(from, to);
    }
}
