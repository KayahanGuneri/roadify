package com.roadify.places.infrastructure.geo;

import java.util.ArrayList;
import java.util.List;

/**
 * Decodes a Google Encoded Polyline string into a list of [lat, lon] points.
 * Geometry must be an encoded polyline (like route-service).
 */
public final class PolylineDecoder {

    private PolylineDecoder() {}

    public static List<LatLon> decode(String encoded) {
        if (encoded == null || encoded.isBlank()) return List.of();

        List<LatLon> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1F) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0) ? ~(result >> 1) : (result >> 1);
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1F) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0) ? ~(result >> 1) : (result >> 1);
            lng += dlng;

            double finalLat = lat / 1e5;
            double finalLng = lng / 1e5;
            poly.add(new LatLon(finalLat, finalLng));
        }

        return poly;
    }

    public record LatLon(double lat, double lon) {}
}
