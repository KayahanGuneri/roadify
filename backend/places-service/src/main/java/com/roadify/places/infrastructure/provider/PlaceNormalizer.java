package com.roadify.places.infrastructure.provider;

import com.roadify.places.domain.Place;
import com.roadify.places.domain.PlaceCategory;

import java.util.List;

/**
 * Normalizes raw provider data to domain Place model.
 */
public interface PlaceNormalizer {

    List<Place> normalize(List<RawPlace> rawPlaces, String routeGeometry);

    PlaceCategory mapCategory(String providerCategory);
}
