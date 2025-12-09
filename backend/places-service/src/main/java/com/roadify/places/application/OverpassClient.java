package com.roadify.places.application;

import com.roadify.places.infrastructure.provider.RawPlace;

import java.util.List;

public interface OverpassClient {

    List<RawPlace> fetchPlaces(String routeGeometry);
}
