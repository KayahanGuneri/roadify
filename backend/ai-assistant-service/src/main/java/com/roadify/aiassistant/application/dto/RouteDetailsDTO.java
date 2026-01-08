package com.roadify.aiassistant.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Route details as returned by route-service via gateway.
 *
 * This closely matches com.roadify.route.api.dto.RouteDTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteDetailsDTO {

    private String id;
    private double distanceKm;
    private double durationMinutes;
    private String geometry;
    private double fromLat;
    private double fromLng;
    private double toLat;
    private double toLng;
}
