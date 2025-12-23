package com.roadify.tripplanner.infrastructure.kafka.event;

import java.time.Instant;

public record TripCreatedEvent(
        String eventId,
        String eventType,
        Instant occurredAt,
        String tripId,
        String userId,
        String routeId
) {}
