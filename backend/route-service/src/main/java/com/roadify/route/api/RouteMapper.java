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

        return new RouteDTO(
                route.getId().toString(),
                route.getDistanceKm(),
                route.getDurationMinutes(),
                route.getGeometry(),
                route.getFromLat(),
                route.getFromLng(),
                route.getToLat(),
                route.getToLng()
        );
    }
}
