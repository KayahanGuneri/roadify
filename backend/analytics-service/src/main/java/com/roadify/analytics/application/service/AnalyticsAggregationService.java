package com.roadify.analytics.application.service;

import com.roadify.analytics.domain.repository.AiUsageStatsRepository;
import com.roadify.analytics.domain.repository.DailyRouteUsageRepository;
import com.roadify.analytics.domain.repository.PopularPlaceCategoriesRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Service responsible for updating analytics aggregates when events occur.
 *
 * Türkçe Özet:
 * Kafka event'leri geldiğinde aggregate tablolarını güncelleyen servis.
 */
@Service
public class AnalyticsAggregationService {

    private final DailyRouteUsageRepository dailyRouteUsageRepository;
    private final PopularPlaceCategoriesRepository popularPlaceCategoriesRepository;
    private final AiUsageStatsRepository aiUsageStatsRepository;

    public AnalyticsAggregationService(
            DailyRouteUsageRepository dailyRouteUsageRepository,
            PopularPlaceCategoriesRepository popularPlaceCategoriesRepository,
            AiUsageStatsRepository aiUsageStatsRepository
    ) {
        this.dailyRouteUsageRepository = dailyRouteUsageRepository;
        this.popularPlaceCategoriesRepository = popularPlaceCategoriesRepository;
        this.aiUsageStatsRepository = aiUsageStatsRepository;
    }

    public void handleRouteCreated(LocalDate date) {
        dailyRouteUsageRepository.incrementRouteCount(date);
    }

    public void handlePlacesFetched(LocalDate date, String category, int placeCount) {
        if (placeCount <= 0) {
            return;
        }
        popularPlaceCategoriesRepository.incrementCategoryCount(date, category, placeCount);
    }

    public void handleAiRecommendationRequested(LocalDate date) {
        aiUsageStatsRepository.incrementRequestCount(date);
    }

    public void handleAiRecommendationAccepted(LocalDate date) {
        aiUsageStatsRepository.incrementAcceptedCount(date);
    }
}
