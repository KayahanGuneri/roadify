/**
 * types.ts
 *
 * English:
 * Single source of truth for navigation param types.
 *
 * Türkçe Özet:
 * Navigation param tipleri tek yerden yönetilir.
 */

export type RootStackParamList = {
    // Auth
    Login: undefined;
    Register: undefined;
    AuthCallback: { code?: string | null; session_state?: string | null } | undefined;

    // App
    Home: undefined;
    RouteSelection: undefined;

    RoutePreview: {
        routeId: string;
        fromCity: string;
        toCity: string;
    };

    RouteMapFull: { routeId: string };
    PlacesList: { routeId: string };

    TripPlanner: { tripId?: string | null } | undefined;

    AIScreen: undefined;
    AnalyticsScreen: undefined;
};
