package com.roadify.route.application;

import com.roadify.route.domain.Route;

/**
 * Port for computing routes using an external routing provider (OpenRouteService).
 */
public interface OrsClient {

    /**
     * Compute a route between two points using an external routing API.
     *
     * @param fromLat starting latitude
     * @param fromLng starting longitude
     * @param toLat   destination latitude
     * @param toLng   destination longitude
     * @return computed Route (without id, persistence layer can assign one)
     */
    Route computeRoute(double fromLat, double fromLng, double toLat, double toLng);
}
