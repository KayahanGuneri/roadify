// Data shape returned by backend for a route
export interface RouteDTO {
    id: string;
    distanceKm: number;
    durationMinutes: number;
    geometry?: string; // polyline/geojson etc. - optional for now
    fromLat: number;
    fromLng: number;
    toLat: number;
    toLng: number;
}

// Body we send when asking for a route preview
export interface RoutePreviewRequestDTO {
    fromLat: number;
    fromLng: number;
    toLat: number;
    toLng: number;
}
