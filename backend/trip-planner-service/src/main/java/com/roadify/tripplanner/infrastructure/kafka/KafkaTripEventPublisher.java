package com.roadify.tripplanner.infrastructure.kafka;

import com.roadify.tripplanner.application.port.TripEventPublisher;
import com.roadify.tripplanner.infrastructure.kafka.event.TripCreatedEvent;
import com.roadify.tripplanner.infrastructure.kafka.event.TripStopsAddedEvent;
import com.roadify.tripplanner.infrastructure.kafka.event.TripStopsRemovedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class KafkaTripEventPublisher implements TripEventPublisher {

    private static final String TOPIC_TRIP_CREATED = "trip.created";
    private static final String TOPIC_STOP_ADDED   = "trip.stop.added";
    private static final String TOPIC_STOP_REMOVED = "trip.stop.removed";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaTripEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishTripCreated(String tripId, String userId, String routeId) {
        TripCreatedEvent payload = new TripCreatedEvent(
                UUID.randomUUID().toString(),
                TOPIC_TRIP_CREATED,
                Instant.now(),
                tripId,
                userId,
                routeId
        );

        sendAfterCommit(TOPIC_TRIP_CREATED, tripId, payload);
    }

    @Override
    public void publishTripStopsAdded(String tripId, String userId, List<String> stopIds) {
        TripStopsAddedEvent payload = new TripStopsAddedEvent(
                UUID.randomUUID().toString(),
                TOPIC_STOP_ADDED,
                Instant.now(),
                tripId,
                userId,
                stopIds
        );

        sendAfterCommit(TOPIC_STOP_ADDED, tripId, payload);
    }

    @Override
    public void publishTripStopsRemoved(String tripId, String userId, List<String> stopIds) {
        TripStopsRemovedEvent payload = new TripStopsRemovedEvent(
                UUID.randomUUID().toString(),
                TOPIC_STOP_REMOVED,
                Instant.now(),
                tripId,
                userId,
                stopIds
        );

        sendAfterCommit(TOPIC_STOP_REMOVED, tripId, payload);
    }

    private void sendAfterCommit(String topic, String key, Object payload) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            kafkaTemplate.send(topic, key, payload);
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                kafkaTemplate.send(topic, key, payload);
            }
        });
    }
}
