package com.roadify.route.api;

import com.roadify.route.api.dto.RouteDTO;
import com.roadify.route.domain.Route;

/**
 * Simple mapper between Route domain model and RouteDTO.
 */
public final class RouteMapper {

    private RouteMapper() {
        // utility class
    }

    public static RouteDTO toDto(Route route) {
        if (route == null) {
            return null;
        }

        return RouteDTO.builder()
                .id(route.getId().toString())
                .fromLat(route.getFromLat())
                .fromLng(route.getFromLng())
                .toLat(route.getToLat())
                .toLng(route.getToLng())
                .distanceKm(route.getDistanceKm())
                .durationMinutes(route.getDurationMinutes())
                .geometry(route.getGeometry())
                .build();
    }
}
