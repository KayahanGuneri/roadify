import type { RouteDTO, RoutePreviewRequestDTO } from '../types/routes';

const ROUTE_SERVICE_BASE_URL = 'http://10.0.2.2:8082';

function normalizeRoute(raw: any): RouteDTO {
    // Accept multiple possible shapes
    const distanceKm =
        raw?.distanceKm ??
        raw?.distance_km ??
        (raw?.distanceMeters != null ? raw.distanceMeters / 1000 : undefined) ??
        (raw?.distance_meters != null ? raw.distance_meters / 1000 : undefined) ??
        0;

    const durationMinutes =
        raw?.durationMinutes ??
        raw?.duration_minutes ??
        (raw?.durationSeconds != null ? raw.durationSeconds / 60 : undefined) ??
        (raw?.duration_seconds != null ? raw.duration_seconds / 60 : undefined) ??
        0;

    return {
        id: String(raw?.id ?? ''),
        distanceKm: Number(distanceKm) || 0,
        durationMinutes: Math.round(Number(durationMinutes) || 0),
        geometry: raw?.geometry ?? raw?.polyline ?? raw?.encodedPolyline,
        fromLat: Number(raw?.fromLat ?? raw?.from_lat ?? 0),
        fromLng: Number(raw?.fromLng ?? raw?.from_lng ?? 0),
        toLat: Number(raw?.toLat ?? raw?.to_lat ?? 0),
        toLng: Number(raw?.toLng ?? raw?.to_lng ?? 0),
    };
}

/**
 * POST /v1/routes/preview
 */
export async function previewRoute(body: RoutePreviewRequestDTO): Promise<RouteDTO> {
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

    const raw = await res.json();
    console.log('previewRoute raw:', raw);
    return normalizeRoute(raw);
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

    const raw = await res.json();
    console.log('getRouteById raw:', raw);
    return normalizeRoute(raw);
}
