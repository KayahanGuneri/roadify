package com.roadify.route.infrastructure.kafka;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

/**
 * Event published when a new route has been computed and persisted.
 */
@Value
@Builder
public class RouteCreatedEvent {

    Instant occurredAt;

    String routeId;
    double fromLat;
    double fromLng;
    double toLat;
    double toLng;
    double distanceKm;
    double durationMinutes;
}
