package com.roadify.places.infrastructure.route;

import lombok.Value;

/**
 * Minimal route information fetched from route-service.
 */
@Value
public class RouteSummary {
    String id;
    double distanceKm;
    double durationMinutes;
    String geometry; // same encoding as route-service (e.g. polyline)
}
