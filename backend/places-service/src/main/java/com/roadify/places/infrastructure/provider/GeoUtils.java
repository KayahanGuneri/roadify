package com.roadify.places.infrastructure.provider;

import java.util.List;

public final class GeoUtils {

    private GeoUtils() {}

    private static final double EARTH_RADIUS_M = 6_371_000.0;

    /** Haversine distance in meters. */
    public static double haversineMeters(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_M * c;
    }

    /**
     * Minimum distance (meters) from a point to a polyline.
     * Approach:
     * - Convert lat/lon to a local tangent plane approximation around the point
     * - Compute point-to-segment distance in meters for each segment
     * This is accurate enough for short distances (detour filters like 0-5km).
     */
    public static double minDistanceMetersToPolyline(double pLat, double pLon, List<LatLon> polyline) {
        if (polyline == null || polyline.size() < 2) return Double.POSITIVE_INFINITY;

        // Precompute scale factors (meters per degree) around pLat
        double metersPerDegLat = 111_320.0;
        double metersPerDegLon = 111_320.0 * Math.cos(Math.toRadians(pLat));

        // Convert point P to local XY (0,0)
        double px = 0.0;
        double py = 0.0;

        double minDist = Double.POSITIVE_INFINITY;

        for (int i = 0; i < polyline.size() - 1; i++) {
            LatLon a = polyline.get(i);
            LatLon b = polyline.get(i + 1);

            // Convert A and B to local XY relative to P
            double ax = (a.getLon() - pLon) * metersPerDegLon;
            double ay = (a.getLat() - pLat) * metersPerDegLat;

            double bx = (b.getLon() - pLon) * metersPerDegLon;
            double by = (b.getLat() - pLat) * metersPerDegLat;

            double dist = pointToSegmentDistance(px, py, ax, ay, bx, by);
            if (dist < minDist) minDist = dist;
        }

        return minDist;
    }

    /** Distance from point P to segment AB in 2D plane (meters). */
    private static double pointToSegmentDistance(double px, double py,
                                                 double ax, double ay,
                                                 double bx, double by) {
        double abx = bx - ax;
        double aby = by - ay;
        double apx = px - ax;
        double apy = py - ay;

        double abLen2 = abx * abx + aby * aby;
        if (abLen2 == 0.0) {
            // A and B are same point
            return Math.sqrt(apx * apx + apy * apy);
        }

        double t = (apx * abx + apy * aby) / abLen2;
        if (t < 0.0) t = 0.0;
        else if (t > 1.0) t = 1.0;

        double closestX = ax + t * abx;
        double closestY = ay + t * aby;

        double dx = px - closestX;
        double dy = py - closestY;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static double round(double value, int decimals) {
        double pow = Math.pow(10, decimals);
        return Math.round(value * pow) / pow;
    }
}
