package com.roadify.places.infrastructure.provider;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Raw place returned by external providers (Geoapify, Overpass, etc).
 * Keep provider-specific fields here and normalize later.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawPlace {

    private String provider;
    private String externalId;

    private String name;

    /**
     * Primary category tag (legacy / fallback).
     * Example: "catering.restaurant" or "restaurant"
     */
    private String categoryTag;

    /**
     * Provider may return multiple categories.
     * Example (Geoapify): ["catering.restaurant", "catering.fast_food", ...]
     */
    private List<String> categories;

    private double latitude;
    private double longitude;

    private Double rating; // nullable
}
