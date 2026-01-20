package com.roadify.analytics.application.service;

import com.roadify.analytics.api.dto.AiUsageDTO;
import com.roadify.analytics.api.dto.AnalyticsOverviewDTO;
import com.roadify.analytics.api.dto.PopularCategoryDTO;
import com.roadify.analytics.domain.model.AiUsageStat;
import com.roadify.analytics.domain.model.DailyRouteUsage;
import com.roadify.analytics.domain.model.PopularPlaceCategoryStat;
import com.roadify.analytics.domain.repository.AiUsageStatsRepository;
import com.roadify.analytics.domain.repository.DailyRouteUsageRepository;
import com.roadify.analytics.domain.repository.PopularPlaceCategoriesRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Read-side service for analytics queries.
 *
 * Türkçe Özet:
 * Analytics REST endpoint'lerinin ihtiyaç duyduğu okuma işlemlerini yapan servis.
 */
@Service
public class AnalyticsReadService {

    private final DailyRouteUsageRepository dailyRouteUsageRepository;
    private final PopularPlaceCategoriesRepository popularPlaceCategoriesRepository;
    private final AiUsageStatsRepository aiUsageStatsRepository;

    public AnalyticsReadService(
            DailyRouteUsageRepository dailyRouteUsageRepository,
            PopularPlaceCategoriesRepository popularPlaceCategoriesRepository,
            AiUsageStatsRepository aiUsageStatsRepository
    ) {
        this.dailyRouteUsageRepository = dailyRouteUsageRepository;
        this.popularPlaceCategoriesRepository = popularPlaceCategoriesRepository;
        this.aiUsageStatsRepository = aiUsageStatsRepository;
    }

    public AnalyticsOverviewDTO getOverview(LocalDate from, LocalDate to) {
        List<DailyRouteUsage> usageList = dailyRouteUsageRepository.findBetween(from, to);
        List<AiUsageStat> aiUsageList = aiUsageStatsRepository.findBetween(from, to);

        int totalRoutes = usageList.stream()
                .mapToInt(DailyRouteUsage::getRouteCount)
                .sum();

        int totalAiRequests = aiUsageList.stream()
                .mapToInt(AiUsageStat::getRequestCount)
                .sum();

        int totalAiAccepted = aiUsageList.stream()
                .mapToInt(AiUsageStat::getAcceptedCount)
                .sum();

        return new AnalyticsOverviewDTO(
                from,
                to,
                totalRoutes,
                totalAiRequests,
                totalAiAccepted
        );
    }

    public List<PopularCategoryDTO> getPopularCategories(LocalDate from, LocalDate to, int limit) {
        List<PopularPlaceCategoryStat> stats =
                popularPlaceCategoriesRepository.findTopCategories(from, to, limit);

        return stats.stream()
                .map(stat -> new PopularCategoryDTO(
                        stat.getDate(),
                        stat.getCategory(),
                        stat.getCount()
                ))
                .collect(Collectors.toList());
    }

    public List<AiUsageDTO> getAiUsage(LocalDate from, LocalDate to) {
        List<AiUsageStat> stats = aiUsageStatsRepository.findBetween(from, to);

        return stats.stream()
                .map(stat -> new AiUsageDTO(
                        stat.getDate(),
                        stat.getRequestCount(),
                        stat.getAcceptedCount(),
                        stat.getAcceptanceRate()
                ))
                .collect(Collectors.toList());
    }
}
