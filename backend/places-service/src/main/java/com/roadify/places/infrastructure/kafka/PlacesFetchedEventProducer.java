package com.roadify.places.infrastructure.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlacesFetchedEventProducer {

    private final KafkaTemplate<String, PlacesFetchedEvent> kafkaTemplate;

    private static final String TOPIC = "places.fetched";

    public void publish(PlacesFetchedEvent event) {
        kafkaTemplate.send(TOPIC, event.getRouteId(), event);
    }
}
