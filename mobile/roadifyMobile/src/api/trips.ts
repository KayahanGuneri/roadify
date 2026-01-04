import { gatewayClient } from './gatewayClient';
import type { CreateTripRequestDTO, TripDTO, UpdateTripStopsRequestDTO } from '../types/trips';

export async function createTrip(request: CreateTripRequestDTO): Promise<TripDTO> {
    const res = await gatewayClient.post<TripDTO>('/trips/v1/trips', request);
    return normalizeTrip(res.data);
}

export async function getTrip(tripId: string): Promise<TripDTO> {
    const res = await gatewayClient.get<TripDTO>(`/trips/v1/trips/${tripId}`);
    return normalizeTrip(res.data);
}

export async function updateTripStops(
    tripId: string,
    request: UpdateTripStopsRequestDTO,
): Promise<TripDTO> {
    const res = await gatewayClient.put<TripDTO>(
        `/trips/v1/trips/${tripId}/stops`,
        request,
    );
    return normalizeTrip(res.data);
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
