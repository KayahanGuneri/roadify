package com.roadify.places.infrastructure.provider;

import java.util.ArrayList;
import java.util.List;

/**
 * Decodes Google encoded polyline strings into a list of LatLon points.
 *
 * If route-service uses a different encoding (GeoJSON, WKT, etc.),
 * replace this decoder accordingly.
 */
public final class PolylineUtils {

    private PolylineUtils() {}

    public static List<LatLon> decode(String encoded) {
        if (encoded == null || encoded.isBlank()) return List.of();

        List<LatLon> points = new ArrayList<>();

        int index = 0;
        int lat = 0;
        int lng = 0;

        while (index < encoded.length()) {
            int result = 0;
            int shift = 0;
            int b;

            // latitude
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlat = ((result & 1) != 0) ? ~(result >> 1) : (result >> 1);
            lat += dlat;

            // longitude
            result = 0;
            shift = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlng = ((result & 1) != 0) ? ~(result >> 1) : (result >> 1);
            lng += dlng;

            double finalLat = lat / 1e5;
            double finalLng = lng / 1e5;

            points.add(new LatLon(finalLat, finalLng));
        }

        return points;
    }
}
