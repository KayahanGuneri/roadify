import { gatewayClient } from './gatewayClient';
import type { PlaceDTO, PlacesFilters } from '../types/places';

/**
 * Fetch places along a route with optional filters.
 *
 * GET /v1/routes/{routeId}/places
 */
export async function fetchPlaces(
    routeId: string,
    filters: PlacesFilters,
): Promise<PlaceDTO[]> {
    const params: Record<string, string | number> = {};

    if (filters.category && filters.category.trim() !== '') {
        params.category = filters.category.trim();
    }

    if (filters.minRating !== undefined) {
        params.minRating = filters.minRating;
    }

    if (filters.maxDetourKm !== undefined) {
        params.maxDetourKm = filters.maxDetourKm;
    }

    console.log('[fetchPlaces]', {
        routeId,
        params,
        rawFilters: filters,
    });

    const response = await gatewayClient.get<PlaceDTO[]>(
        `/v1/routes/${routeId}/places`,
        { params },
    );

    return response.data;
}
