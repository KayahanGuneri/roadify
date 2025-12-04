package com.roadify.route.infrastructure.kafka;

import com.roadify.route.domain.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Producer responsible for publishing route-related domain events.
 *
 * For now, this is a stub that only logs the event instead of sending to Kafka.
 */
@Component
public class RouteEventProducer {

    private static final Logger log = LoggerFactory.getLogger(RouteEventProducer.class);
    private static final String ROUTE_CREATED_TOPIC = "route.created";

    /**
     * "Publish" a route.created event for the given route.
     * Currently only logs; real Kafka integration will be added later.
     */
    public void sendRouteCreated(Route route) {
        RouteCreatedEvent event = RouteCreatedEvent.builder()
                .routeId(route.getId().toString())
                .fromLat(route.getFromLat())
                .fromLng(route.getFromLng())
                .toLat(route.getToLat())
                .toLng(route.getToLng())
                .distanceKm(route.getDistanceKm())
                .durationMinutes(route.getDurationMinutes())
                .build();

        log.info("[STUB] route.created event would be published. topic={}, routeId={}, from=({}, {}), to=({}, {})",
                ROUTE_CREATED_TOPIC,
                event.getRouteId(),
                event.getFromLat(), event.getFromLng(),
                event.getToLat(), event.getToLng()
        );
    }
}
