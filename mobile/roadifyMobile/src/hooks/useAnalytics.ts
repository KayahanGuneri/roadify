// src/hooks/useAnalytics.ts

/**
 * Analytics queries (parallel fetch).
 */

import { useMemo } from 'react';
import { useQuery } from '@tanstack/react-query';
import { getAiUsage, getOverview, getPopularCategories } from '../api/analytics';
import type { AnalyticsOverviewDTO, PopularCategoryDTO, AiUsageDTO } from '../types/analytics';
import { lastNDaysRange } from '../lib/dateRange';

type UseAnalyticsResult = {
    range: { from: string; to: string };

    overview: AnalyticsOverviewDTO | undefined;
    popularCategories: PopularCategoryDTO[] | undefined;
    aiUsage: AiUsageDTO[] | undefined;

    isLoading: boolean;
    isError: boolean;
    error: unknown;

    refetchAll: () => void;
};

export function useAnalytics(): UseAnalyticsResult {
    // Son 30 gün
    const range = useMemo(() => lastNDaysRange(30), []);

    const overviewQuery = useQuery({
        queryKey: ['analytics', 'overview', range.from, range.to],
        queryFn: () => getOverview(range),
    });

    const popularCategoriesQuery = useQuery({
        queryKey: ['analytics', 'popular-categories', range.from, range.to, 10],
        queryFn: () => getPopularCategories(range, 10),
    });

    const aiUsageQuery = useQuery({
        queryKey: ['analytics', 'ai-usage', range.from, range.to],
        queryFn: () => getAiUsage(range),
    });

    const isLoading =
        overviewQuery.isLoading || popularCategoriesQuery.isLoading || aiUsageQuery.isLoading;

    const isError = overviewQuery.isError || popularCategoriesQuery.isError || aiUsageQuery.isError;

    const error = useMemo(() => {
        return overviewQuery.error ?? popularCategoriesQuery.error ?? aiUsageQuery.error ?? null;
    }, [overviewQuery.error, popularCategoriesQuery.error, aiUsageQuery.error]);

    function refetchAll() {
        void overviewQuery.refetch();
        void popularCategoriesQuery.refetch();
        void aiUsageQuery.refetch();
    }

    return {
        range,

        overview: overviewQuery.data,
        popularCategories: popularCategoriesQuery.data,
        aiUsage: aiUsageQuery.data,

        isLoading,
        isError,
        error,

        refetchAll,
    };
}
