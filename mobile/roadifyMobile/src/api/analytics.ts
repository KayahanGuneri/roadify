// src/api/analytics.ts

import { gatewayClient } from './gatewayClient';
import type { AnalyticsOverviewDTO, PopularCategoryDTO, AiUsageDTO } from '../types/analytics';
import type { DateRange } from '../lib/dateRange';

export async function getOverview(range: DateRange): Promise<AnalyticsOverviewDTO> {
    const res = await gatewayClient.get<AnalyticsOverviewDTO>(
        '/mobile/v1/analytics/overview',
        { params: range },
    );
    return res.data;
}

export async function getPopularCategories(
    range: DateRange,
    limit = 10,
): Promise<PopularCategoryDTO[]> {
    const res = await gatewayClient.get<PopularCategoryDTO[]>(
        '/mobile/v1/analytics/popular-categories',
        { params: { ...range, limit } },
    );
    return res.data;
}

export async function getAiUsage(range: DateRange): Promise<AiUsageDTO[]> {
    const res = await gatewayClient.get<AiUsageDTO[]>(
        '/mobile/v1/analytics/ai-usage',
        { params: range },
    );
    return res.data;
}
