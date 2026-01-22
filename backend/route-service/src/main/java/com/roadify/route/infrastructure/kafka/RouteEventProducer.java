package com.roadify.route.infrastructure.kafka;

import com.roadify.route.domain.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class RouteEventProducer {

    private static final Logger log = LoggerFactory.getLogger(RouteEventProducer.class);
    private static final String ROUTE_CREATED_TOPIC = "route.created";

    private final KafkaTemplate<String, RouteCreatedEvent> kafkaTemplate;

    public RouteEventProducer(KafkaTemplate<String, RouteCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendRouteCreated(Route route) {
        RouteCreatedEvent event = RouteCreatedEvent.builder()
                .occurredAt(Instant.now())
                .routeId(route.getId().toString())
                .fromLat(route.getFromLat())
                .fromLng(route.getFromLng())
                .toLat(route.getToLat())
                .toLng(route.getToLng())
                .distanceKm(route.getDistanceKm())
                .durationMinutes(route.getDurationMinutes())
                .build();

        kafkaTemplate.send(ROUTE_CREATED_TOPIC, event.getRouteId(), event);

        log.info("Published route.created event. topic={}, key(routeId)={}, occurredAt={}",
                ROUTE_CREATED_TOPIC, event.getRouteId(), event.getOccurredAt());
    }
}
