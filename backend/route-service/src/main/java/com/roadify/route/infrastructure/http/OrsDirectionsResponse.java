package com.roadify.route.infrastructure.http;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Minimal response mapping for ORS Directions API.
 * We only care about:
 * - routes[0].summary.distance (meters)
 * - routes[0].summary.duration (seconds)
 * - routes[0].geometry (kept as Object for flexibility)
 */
@Setter
@Getter
public class OrsDirectionsResponse {

    private List<OrsRoute> routes;

    @Setter
    @Getter
    public static class OrsRoute {
        private OrsSummary summary;
        private Object geometry; // keep generic, we'll serialize to JSON string

    }

    @Setter
    @Getter
    public static class OrsSummary {
        private double distance; // meters
        private double duration; // seconds

    }
}
