package com.roadify.route.domain;

import lombok.Builder;
import lombok.Value;

/**
 * Domain model representing a route between two points.
 */
@Value
@Builder
public class Route {

    String id;              // UUID as String
    double fromLat;
    double fromLng;
    double toLat;
    double toLng;
    double distanceKm;
    double durationMinutes;
    String geometry;        // Encoded polyline or similar representation
}
