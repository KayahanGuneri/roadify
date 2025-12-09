package com.roadify.places.infrastructure.provider;

import lombok.Builder;
import lombok.Value;

/**
 * Provider-agnostic raw place structure before normalization.
 */
@Value
@Builder
public class RawPlace {
    String provider;   // e.g. "OpenTripMap" or "Overpass"
    String externalId;
    String name;
    String categoryTag;   // provider-specific category / tag
    double latitude;
    double longitude;
    Double rating;
}
