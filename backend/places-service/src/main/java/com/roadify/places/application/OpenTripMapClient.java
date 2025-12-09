package com.roadify.places.application;

import com.roadify.places.infrastructure.provider.RawPlace;

import java.util.List;

/**
 * Abstraction for OpenTripMap API.
 */
public interface OpenTripMapClient {

    List<RawPlace> fetchPlaces(String routeGeometry);
}
