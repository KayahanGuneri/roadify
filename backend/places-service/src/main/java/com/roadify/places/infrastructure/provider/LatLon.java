package com.roadify.places.infrastructure.provider;

import lombok.Value;

/** Simple immutable lat/lon value object. */
@Value
public class LatLon {
    double lat;
    double lon;
}
