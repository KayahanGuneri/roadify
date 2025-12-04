package com.roadify.route.domain;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Route {
    private UUID id;
    private double fromLat;
    private double fromLng;
    private double toLat;
    private double toLng;
    private double distanceKm;
    private double durationMinutes;
    private String geometry;
}
