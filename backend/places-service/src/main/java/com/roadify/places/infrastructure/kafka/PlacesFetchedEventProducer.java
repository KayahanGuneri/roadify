package com.roadify.places.infrastructure.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Profile("docker")
@Component
@RequiredArgsConstructor
public class PlacesFetchedEventProducer {

    private final KafkaTemplate<String, PlacesFetchedEvent> kafkaTemplate;

    public void publish(PlacesFetchedEvent event) {
        kafkaTemplate.send("places.fetched", event);
    }
}
