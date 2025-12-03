package com.roadify.route.api.dto;

import lombok.Data;

/**
 * Request body for previewing a route between two coordinates.
 */
@Data
public class RoutePreviewRequestDTO {

    private double fromLat;
    private double fromLng;
    private double toLat;
    private double toLng;
}
