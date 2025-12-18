import { useQuery } from '@tanstack/react-query';
import type { PlaceDTO, PlacesFilters } from '../types/places';
import { fetchPlaces } from '../api/places';

/**
 * usePlaces
 *
 * English:
 * React Query hook to fetch places along a route with filtering.
 *
 * Türkçe Özet:
 * Rota üzerindeki mekanları filtrelerle çeken React Query hook'u.
 * Filtre değişince queryKey değişir ve otomatik refetch olur.
 */
export function usePlaces(routeId: string, filters: PlacesFilters) {
    const categoryKey = filters.category ?? '';
    const minRatingKey =
        filters.minRating !== undefined ? String(filters.minRating) : '';
    const maxDetourKmKey =
        filters.maxDetourKm !== undefined ? String(filters.maxDetourKm) : '';

    return useQuery<PlaceDTO[], Error>({
        queryKey: ['places', routeId, categoryKey, minRatingKey, maxDetourKmKey],
        queryFn: () => fetchPlaces(routeId, filters),
        enabled: Boolean(routeId),
        staleTime: 30_000,
    });
}
