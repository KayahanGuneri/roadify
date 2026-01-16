// mobile/roadifyMobile/src/api/trips.ts

import { gatewayClient } from './gatewayClient';
import type { CreateTripRequestDTO, TripDTO, UpdateTripStopsRequestDTO } from '../types/trips';

/**
 * Create a trip
 * POST /api/trips  -> gateway -> /v1/trips (trip-planner-service)
 */
export async function createTrip(request: CreateTripRequestDTO): Promise<TripDTO> {
    try {
        const res = await gatewayClient.post<TripDTO>('/trips', request);
        console.log('[createTrip][OK]', res.status, res.data.id);
        return normalizeTrip(res.data);
    } catch (e: any) {
        const status = e?.response?.status;
        const data = e?.response?.data;
        const url = e?.config?.url;
        console.log('[createTrip][FAIL]', { status, url, data });
        throw e;
    }
}

/**
 * Get trip detail
 * GET /api/trips/{tripId} -> /v1/trips/{tripId}
 */
export async function getTrip(tripId: string): Promise<TripDTO> {
    try {
        const res = await gatewayClient.get<TripDTO>(`/trips/${tripId}`);
        console.log('[getTrip][OK]', res.status, res.data.id);
        return normalizeTrip(res.data);
    } catch (e: any) {
        const status = e?.response?.status;
        const data = e?.response?.data;
        const url = e?.config?.url;
        console.log('[getTrip][FAIL]', { status, url, data });
        throw e;
    }
}

/**
 * Update trip stops
 * PUT /api/trips/{tripId}/stops -> /v1/trips/{tripId}/stops
 */
export async function updateTripStops(
    tripId: string,
    request: UpdateTripStopsRequestDTO,
): Promise<TripDTO> {
    try {
        const res = await gatewayClient.put<TripDTO>(
            `/trips/${tripId}/stops`,
            request,
        );
        console.log('[updateTripStops][OK]', res.status, res.data.id);
        return normalizeTrip(res.data);
    } catch (e: any) {
        const status = e?.response?.status;
        const data = e?.response?.data;
        const url = e?.config?.url;
        console.log('[updateTripStops][FAIL]', { status, url, data });
        throw e;
    }
}

function normalizeTrip(trip: TripDTO): TripDTO {
    return {
        ...trip,
        createdAt: trip.createdAt,
        stops: (trip.stops ?? []).map((s) => ({
            ...s,
            plannedArrivalTime: s.plannedArrivalTime ?? null,
            plannedDurationMinutes: s.plannedDurationMinutes ?? null,
        })),
    };
}
