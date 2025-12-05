package com.roadify.route.api.dto;

import lombok.Builder;
import lombok.Value;

/**
 * DTO used to expose route information to API consumers.
 */

@Builder
public record RouteDTO(
        String id,
        double distanceKm,
        double durationMinutes,
        String geometry,
        double fromLat,
        double fromLng,
        double toLat,
        double toLng
) {}

