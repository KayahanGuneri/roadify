package com.roadify.places.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model representing a place along a route.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Place {

    private String id;
    private String name;
    private PlaceCategory category;
    private double latitude;
    private double longitude;
    private Double rating;   // nullable
    private double detourKm; // primitive: never null, defaults to 0.0 if not set
}
