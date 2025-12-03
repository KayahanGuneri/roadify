package com.roadify.route.infrastructure.http;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Minimal request body for ORS Directions.
 */
@Data
@AllArgsConstructor
public class OrsDirectionsRequest {
    private double[][] coordinates;
}
