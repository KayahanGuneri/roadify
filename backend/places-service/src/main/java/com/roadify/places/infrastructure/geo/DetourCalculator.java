package com.roadify.places.infrastructure.geo;

import java.util.List;

import static java.lang.Math.*;

public final class DetourCalculator {

    private DetourCalculator() {}

    /**
     * Computes detour km as: detour = 2 * (min distance from place to route polyline).
     * Approximation: going out and returning to route at the closest point.
     */
    public static double computeDetourKm(double placeLat, double placeLon, List<PolylineDecoder.LatLon> route) {
        if (route == null || route.size() < 2) return 0.0;

        double minKm = Double.POSITIVE_INFINITY;

        for (int i = 0; i < route.size() - 1; i++) {
            PolylineDecoder.LatLon a = route.get(i);
            PolylineDecoder.LatLon b = route.get(i + 1);

            double d = distancePointToSegmentKm(
                    placeLat, placeLon,
                    a.lat(), a.lon(),
                    b.lat(), b.lon()
            );

            if (d < minKm) minKm = d;
        }

        if (!Double.isFinite(minKm)) return 0.0;

        double detour = 2.0 * minKm;
        return round3(detour);
    }

    // --- Geometry helpers (equirectangular projection for local distances) ---

    private static double distancePointToSegmentKm(
            double plat, double plon,
            double alat, double alon,
            double blat, double blon
    ) {
        // Project to a local plane around the point (good enough for city-scale detours)
        double lat0 = toRadians(plat);

        double ax = lonToXKm(alon, plon, lat0);
        double ay = latToYKm(alat, plat);
        double bx = lonToXKm(blon, plon, lat0);
        double by = latToYKm(blat, plat);

        // Point is origin (0,0)
        double px = 0.0;
        double py = 0.0;

        double vx = bx - ax;
        double vy = by - ay;

        double wx = px - ax;
        double wy = py - ay;

        double c1 = wx * vx + wy * vy;
        if (c1 <= 0) return hypot(ax - px, ay - py);

        double c2 = vx * vx + vy * vy;
        if (c2 <= c1) return hypot(bx - px, by - py);

        double t = c1 / c2;
        double projx = ax + t * vx;
        double projy = ay + t * vy;

        return hypot(projx - px, projy - py);
    }

    private static double lonToXKm(double lon, double lon0, double lat0Rad) {
        // x ~ (lon-lon0) * cos(lat0) * EarthRadius
        double dLon = toRadians(lon - lon0);
        return dLon * cos(lat0Rad) * 6371.0;
    }

    private static double latToYKm(double lat, double lat0) {
        double dLat = toRadians(lat - lat0);
        return dLat * 6371.0;
    }

    private static double round3(double v) {
        return Math.round(v * 1000.0) / 1000.0;
    }
}
