package com.roadify.route.infrastructure.http;

import lombok.Getter;
import lombok.Setter;

/**
 * Minimal request body for ORS Directions API.
 * ORS expects:
 * {
 *   "coordinates": [
 *     [lon1, lat1],
 *     [lon2, lat2]
 *   ],
 *   "format": "json"
 * }
 */
@Setter
@Getter
public class OrsDirectionsRequest {

    private String format = "json"; // ORS default response format
    private double[][] coordinates;

    public OrsDirectionsRequest() {
        // Default constructor for Jackson
    }

    public OrsDirectionsRequest(double[][] coordinates) {
        this.coordinates = coordinates;
    }

}
