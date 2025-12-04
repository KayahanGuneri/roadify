// src/api/routes.ts
import { httpClient } from './httpClient';
import { RouteDTO, RoutePreviewRequestDTO } from '../types/routes';

/**
 * Ask route-service to calculate a route preview.
 * POST /v1/routes/preview
 */
export async function previewRoute(
    body: RoutePreviewRequestDTO,
): Promise<RouteDTO> {
    const response = await httpClient.post<RouteDTO>(
        '/v1/routes/preview',
        body,
    );
    return response.data;
}

/**
 * Fetch a route by id.
 * GET /v1/routes/{id}
 */
export async function getRouteById(id: string): Promise<RouteDTO> {
    const response = await httpClient.get<RouteDTO>(`/v1/routes/${id}`);
    return response.data;
}
