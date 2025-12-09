package com.roadify.places.domain;

/**
 * High-level category for places along the route.
 * Provider-specific tags (amenity, tourism, etc.) are mapped to this enum.
 */
public enum PlaceCategory {
    FOOD,
    CAFE,
    FUEL,
    TOURIST,
    MARKET,
    WC,
    HOTEL,
    CAMPING,
    SHOP,
    OTHER
}
