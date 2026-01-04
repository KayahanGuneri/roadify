// src/api/routes.ts
import type { RouteDTO, RoutePreviewRequestDTO } from '../types/routes';

const ROUTE_SERVICE_BASE_URL = 'http://10.0.2.2:8082';

/**
 * POST /v1/routes/preview
 */
export async function previewRoute(
    body: RoutePreviewRequestDTO,
): Promise<RouteDTO> {
    const res = await fetch(`${ROUTE_SERVICE_BASE_URL}/v1/routes/preview`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body),
    });

    if (!res.ok) {
        const text = await res.text().catch(() => '');
        console.log('previewRoute error:', res.status, text);
        throw new Error(`previewRoute failed (${res.status}) ${text || ''}`);
    }

    return (await res.json()) as RouteDTO;
}

/**
 * GET /v1/routes/{id}
 */
export async function getRouteById(id: string): Promise<RouteDTO> {
    const res = await fetch(`${ROUTE_SERVICE_BASE_URL}/v1/routes/${id}`, {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' },
    });

    if (!res.ok) {
        const text = await res.text().catch(() => '');
        console.log('getRouteById error:', res.status, text);
        throw new Error(`getRouteById failed (${res.status}) ${text || ''}`);
    }

    return (await res.json()) as RouteDTO;
}
