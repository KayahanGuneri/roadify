package com.roadify.route.infrastructure.http;

import lombok.Data;

import java.util.List;

/**
 * DTO that matches OpenRouteService Directions V2 response.
 */
@Data
public class OrsDirectionsResponse {

    private List<OrsRoute> routes;

    @Data
    public static class OrsRoute {
        private OrsSummary summary;
        private String geometry;
    }

    @Data
    public static class OrsSummary {
        private double distance;  // in meters
        private double duration;  // in seconds
    }
}
