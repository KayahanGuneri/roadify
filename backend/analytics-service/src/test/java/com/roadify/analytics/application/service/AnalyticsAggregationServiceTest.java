package com.roadify.analytics.application.service;

import com.roadify.analytics.domain.repository.AiUsageStatsRepository;
import com.roadify.analytics.domain.repository.DailyRouteUsageRepository;
import com.roadify.analytics.domain.repository.PopularPlaceCategoriesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

/**
 * Unit tests for AnalyticsAggregationService.
 *
 * Türkçe Özet:
 * Aggregation servisinin repository çağrılarını doğru yapıp yapmadığını test eder.
 */
class AnalyticsAggregationServiceTest {

    private DailyRouteUsageRepository dailyRouteUsageRepository;
    private PopularPlaceCategoriesRepository popularPlaceCategoriesRepository;
    private AiUsageStatsRepository aiUsageStatsRepository;

    private AnalyticsAggregationService aggregationService;

    @BeforeEach
    void setUp() {
        dailyRouteUsageRepository = mock(DailyRouteUsageRepository.class);
        popularPlaceCategoriesRepository = mock(PopularPlaceCategoriesRepository.class);
        aiUsageStatsRepository = mock(AiUsageStatsRepository.class);

        aggregationService = new AnalyticsAggregationService(
                dailyRouteUsageRepository,
                popularPlaceCategoriesRepository,
                aiUsageStatsRepository
        );
    }

    @Test
    void handleRouteCreated_shouldIncrementDailyRouteUsage() {
        LocalDate today = LocalDate.now();

        aggregationService.handleRouteCreated(today);

        verify(dailyRouteUsageRepository, times(1))
                .incrementRouteCount(today);
    }

    @Test
    void handlePlacesFetched_shouldIncrementCategoryCount_whenPlaceCountPositive() {
        LocalDate today = LocalDate.now();

        aggregationService.handlePlacesFetched(today, "CAFE", 3);

        verify(popularPlaceCategoriesRepository, times(1))
                .incrementCategoryCount(today, "CAFE", 3);
    }

    @Test
    void handlePlacesFetched_shouldDoNothing_whenPlaceCountZeroOrNegative() {
        LocalDate today = LocalDate.now();

        aggregationService.handlePlacesFetched(today, "CAFE", 0);
        aggregationService.handlePlacesFetched(today, "CAFE", -1);

        verifyNoInteractions(popularPlaceCategoriesRepository);
    }

    @Test
    void handleAiRecommendationRequested_shouldIncrementRequestCount() {
        LocalDate today = LocalDate.now();

        aggregationService.handleAiRecommendationRequested(today);

        verify(aiUsageStatsRepository, times(1))
                .incrementRequestCount(today);
    }

    @Test
    void handleAiRecommendationAccepted_shouldIncrementAcceptedCount() {
        LocalDate today = LocalDate.now();

        aggregationService.handleAiRecommendationAccepted(today);

        verify(aiUsageStatsRepository, times(1))
                .incrementAcceptedCount(today);
    }
}
