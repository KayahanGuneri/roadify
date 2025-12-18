/**
 * places.ts
 *
 * English:
 * Shared DTOs and filter types for route-based places.
 *
 * Türkçe Özet:
 * Rota üzerindeki mekanlar için kullanılan DTO ve filtre tipleri.
 */

export type PlaceDTO = {
    id: string;
    name: string;
    category: string;
    rating: number | null;
    detourKm: number;
    latitude: number;
    longitude: number;
};

export type PlacesFilters = {
    category?: string;
    minRating?: number;
    maxDetourKm?: number;
};
