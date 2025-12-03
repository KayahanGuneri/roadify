package com.roadify.route.api.dto;

import lombok.Builder;
import lombok.Value;

/**
 * DTO used to expose route information to API consumers.
 */
@Value
@Builder
public class RouteDTO {

    String id;
    double fromLat;
    double fromLng;
    double toLat;
    double toLng;
    double distanceKm;
    double durationMinutes;
    String geometry;
}
