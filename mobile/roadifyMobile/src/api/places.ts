import { gatewayClient } from './gatewayClient';
import type { PlaceDTO, PlacesFilters } from '../types/places';

/**
 * Fetch places along a route with optional filters.
 *
 * GET /v1/routes/{routeId}/places
 */
export async function fetchPlaces(routeId: string, filters: PlacesFilters): Promise<PlaceDTO[]> {
    const params: Record<string, string | number> = {};

    if (filters.category?.trim()) params.category = filters.category.trim();
    if (filters.minRating !== undefined) params.minRating = filters.minRating;
    if (filters.maxDetourKm !== undefined) params.maxDetourKm = filters.maxDetourKm;

    try {
        const res = await gatewayClient.get<PlaceDTO[]>(
            `/routes/${routeId}/places`,
            { params }
        );

        console.log('[fetchPlaces][OK]', res.status, res.data?.length);
        return res.data;
    } catch (e: any) {
        const status = e?.response?.status;
        const data = e?.response?.data;
        const url = e?.config?.url;
        console.log('[fetchPlaces][FAIL]', { status, url, data });
        throw e;
    }
}

