// src/hooks/useRouteDetails.ts
//
// Roadify – Fetch route details by id
//

/*
import { useQuery } from '@tanstack/react-query';
import { RouteDTO } from '../types/routes';

const API_BASE_URL = 'http://10.0.2.2:8082'; // route-service port'un

async function fetchRouteDetails(routeId: string): Promise<RouteDTO> {
    const url = `${API_BASE_URL}/v1/routes/${routeId}`;

    const response = await fetch(url, {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
        },
    });

    if (!response.ok) {
        // Hata mesajını loglamak için backend body’sini de okuyalım
        let bodyText = '';
        try {
            bodyText = await response.text();
        } catch {
            // ignore
        }
        console.warn('fetchRouteDetails failed', response.status, bodyText);

        throw new Error(
            `Failed to load route details (status ${response.status})`,
        );
    }

    const json = (await response.json()) as RouteDTO;
    return json;
}


 * useRouteDetails
 *
 * @param routeId - Backend'de oluşturulmuş rota id'si
 *
 * React Query kullanarak rota detaylarını çeker.
 * enabled: !!routeId olduğu için routeId gelmeden istek atmaz.


export function useRouteDetails(routeId: string | undefined) {
    return useQuery<RouteDTO, Error>({
        queryKey: ['routeDetails', routeId],
        enabled: !!routeId,
        queryFn: () => fetchRouteDetails(routeId as string),
        staleTime: 1000 * 30, // 30 saniye
    });
}
*/