package com.roadify.route.infrastructure.kafka;

import lombok.Builder;
import lombok.Value;

/**
 * Event published when a new route has been computed and persisted.
 */
@Value
@Builder
public class RouteCreatedEvent {

    String routeId;
    double fromLat;
    double fromLng;
    double toLat;
    double toLng;
    double distanceKm;
    double durationMinutes;
}
