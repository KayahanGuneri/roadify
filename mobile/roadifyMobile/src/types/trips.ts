/**
 * trips.ts
 *
 * English:
 * DTOs for trip-planner-service integration.
 *
 * Türkçe Özet:
 * Trip-planner-service ile konuşan frontend DTO tipleri.
 */

export type TripStopDTO = {
    id: string;
    tripId: string;
    placeId: string;
    orderIndex: number;
    plannedArrivalTime: string | null; // Instant -> ISO string
    plannedDurationMinutes: number | null;
};

export type TripDTO = {
    id: string;
    userId: string;
    routeId: string;
    title: string;
    createdAt: string; // Instant -> ISO string
    stops: TripStopDTO[];
};

export type CreateTripRequestDTO = {
    routeId: string;
    title: string;
};

export type UpdateTripStopsRequestDTO = {
    add: Array<{
        id?: string;
        tripId?: string;
        placeId: string;
        orderIndex: number;
        plannedArrivalTime?: string | null;
        plannedDurationMinutes?: number | null;
    }>;
    removeIds: string[];
};

export type ApiErrorDTO = {
    timestamp: string;
    status: number;
    message: string;
};
