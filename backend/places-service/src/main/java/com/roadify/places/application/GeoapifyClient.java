package com.roadify.places.application;

import com.roadify.places.infrastructure.provider.RawPlace;

import java.util.List;

/**
 * Abstraction for Geoapify Places API.
 */
public interface GeoapifyClient {

    /**
     * Fetch raw places around a route geometry.
     * For now we use a simplified geometry handling (fixed lat/lon).
     *
     * @param routeGeometry encoded route geometry
     * @return list of raw places from Geoapify
     */
    List<RawPlace> fetchPlaces(String routeGeometry);
}
