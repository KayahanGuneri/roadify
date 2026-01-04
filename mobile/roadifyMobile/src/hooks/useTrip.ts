import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import type { CreateTripRequestDTO, TripDTO, UpdateTripStopsRequestDTO } from '../types/trips';
import { createTrip, getTrip, updateTripStops } from '../api/trips';

export function useTrip(accessToken: string | null, tripId: string | null) {
    const qc = useQueryClient();

    const tripQuery = useQuery({
        queryKey: ['trip', tripId],
        queryFn: async (): Promise<TripDTO> => {
            if (!accessToken) throw new Error('Missing access token');
            if (!tripId) throw new Error('Missing tripId');
            return getTrip(tripId);
        },
        enabled: Boolean(accessToken && tripId),
    });

    const createTripMutation = useMutation({
        mutationFn: async (req: CreateTripRequestDTO) => {
            if (!accessToken) throw new Error('Missing access token');
            return createTrip(req);
        },
    });

    const updateStopsMutation = useMutation({
        mutationFn: async (payload: { tripId: string; req: UpdateTripStopsRequestDTO }) => {
            if (!accessToken) throw new Error('Missing access token');
            return updateTripStops(payload.tripId, payload.req);
        },
        onSuccess: (data) => {
            qc.setQueryData(['trip', data.id], data);
        },
    });

    return { tripQuery, createTripMutation, updateStopsMutation };
}
