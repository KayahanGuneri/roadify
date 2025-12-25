package com.roadify.tripplanner.infrastructure.kafka;

import com.roadify.tripplanner.infrastructure.kafka.event.TripCreatedEvent;
import com.roadify.tripplanner.infrastructure.kafka.event.TripStopsAddedEvent;
import com.roadify.tripplanner.infrastructure.kafka.event.TripStopsRemovedEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KafkaTripEventPublisherTest {

    @AfterEach
    void tearDown() {
        // Ensure transaction synchronization state is cleared between tests.
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
        TransactionSynchronizationManager.setActualTransactionActive(false);
    }

    @Test
    void publishTripCreated_whenNoTransaction_shouldSendImmediately() {
        // Arrange
        @SuppressWarnings("unchecked")
        KafkaTemplate<String, Object> kafkaTemplate = mock(KafkaTemplate.class);
        KafkaTripEventPublisher publisher = new KafkaTripEventPublisher(kafkaTemplate);

        // Act
        publisher.publishTripCreated("t1", "u1", "r1");

        // Assert
        ArgumentCaptor<Object> payloadCaptor = ArgumentCaptor.forClass(Object.class);
        verify(kafkaTemplate).send(eq("trip.created"), eq("t1"), payloadCaptor.capture());
        assertTrue(payloadCaptor.getValue() instanceof TripCreatedEvent);
    }

    @Test
    void publishTripStopsAdded_whenTransactionActive_shouldSendAfterCommit() {
        // Arrange
        @SuppressWarnings("unchecked")
        KafkaTemplate<String, Object> kafkaTemplate = mock(KafkaTemplate.class);
        KafkaTripEventPublisher publisher = new KafkaTripEventPublisher(kafkaTemplate);

        TransactionSynchronizationManager.initSynchronization();
        TransactionSynchronizationManager.setActualTransactionActive(true);

        // Act
        publisher.publishTripStopsAdded("t1", "u1", List.of("s1", "s2"));

        // Assert
        verify(kafkaTemplate, never()).send(anyString(), anyString(), any());

        List<TransactionSynchronization> syncs = TransactionSynchronizationManager.getSynchronizations();
        assertEquals(1, syncs.size());

        syncs.get(0).afterCommit();

        ArgumentCaptor<Object> payloadCaptor = ArgumentCaptor.forClass(Object.class);
        verify(kafkaTemplate).send(eq("trip.stop.added"), eq("t1"), payloadCaptor.capture());
        assertTrue(payloadCaptor.getValue() instanceof TripStopsAddedEvent);
    }

    @Test
    void publishTripStopsRemoved_whenTransactionActive_shouldSendAfterCommit() {
        // Arrange
        @SuppressWarnings("unchecked")
        KafkaTemplate<String, Object> kafkaTemplate = mock(KafkaTemplate.class);
        KafkaTripEventPublisher publisher = new KafkaTripEventPublisher(kafkaTemplate);

        TransactionSynchronizationManager.initSynchronization();
        TransactionSynchronizationManager.setActualTransactionActive(true);

        // Act
        publisher.publishTripStopsRemoved("t1", "u1", List.of("s9"));

        // Assert
        verify(kafkaTemplate, never()).send(anyString(), anyString(), any());

        List<TransactionSynchronization> syncs = TransactionSynchronizationManager.getSynchronizations();
        assertEquals(1, syncs.size());

        syncs.get(0).afterCommit();

        ArgumentCaptor<Object> payloadCaptor = ArgumentCaptor.forClass(Object.class);
        verify(kafkaTemplate).send(eq("trip.stop.removed"), eq("t1"), payloadCaptor.capture());
        assertTrue(payloadCaptor.getValue() instanceof TripStopsRemovedEvent);
    }
}
