package com.roadify.tripplanner.infrastructure.kafka.event;

import java.time.Instant;
import java.util.List;

public record TripStopsAddedEvent(
        String eventId,
        String eventType,
        Instant occurredAt,
        String tripId,
        String userId,
        List<String> stopIds
) {}
