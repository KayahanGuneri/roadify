package com.roadify.tripplanner.application.service;

import com.roadify.tripplanner.api.dto.CreateTripRequest;
import com.roadify.tripplanner.api.dto.TripResponse;
import com.roadify.tripplanner.api.dto.TripStopDto;
import com.roadify.tripplanner.api.dto.UpdateTripStopsRequest;
import com.roadify.tripplanner.application.exception.TripNotFoundException;
import com.roadify.tripplanner.application.port.TripEventPublisher;
import com.roadify.tripplanner.application.port.TripRepository;
import com.roadify.tripplanner.application.port.TripStopRepository;
import com.roadify.tripplanner.domain.Trip;
import com.roadify.tripplanner.domain.TripStop;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TripServiceImplTest {

    @Test
    void createTrip_shouldPersistPublishAndReturnResponse() {
        // Arrange
        TripRepository tripRepository = mock(TripRepository.class);
        TripStopRepository tripStopRepository = mock(TripStopRepository.class);
        TripEventPublisher eventPublisher = mock(TripEventPublisher.class);

        TripServiceImpl service = new TripServiceImpl(tripRepository, tripStopRepository, eventPublisher);

        CreateTripRequest request = new CreateTripRequest("  route-1  ", "  My Trip  ");
        ArgumentCaptor<Trip> tripCaptor = ArgumentCaptor.forClass(Trip.class);

        // Act
        TripResponse response = service.createTrip("user-1", request);

        // Assert
        verify(tripRepository).save(tripCaptor.capture());
        Trip saved = tripCaptor.getValue();

        assertEquals("user-1", saved.getUserId());
        assertEquals("route-1", saved.getRouteId());
        assertEquals("My Trip", saved.getTitle());

        verify(eventPublisher).publishTripCreated(saved.getId().toString(), "user-1", "route-1");
        verifyNoInteractions(tripStopRepository);

        assertEquals(saved.getId().toString(), response.getId());
        assertEquals("user-1", response.getUserId());
        assertEquals("route-1", response.getRouteId());
        assertEquals("My Trip", response.getTitle());
        assertEquals(saved.getCreatedAt(), response.getCreatedAt());
        assertNotNull(response.getStops());
        assertTrue(response.getStops().isEmpty());
    }

    @Test
    void createTrip_whenRequestInvalid_shouldThrowIllegalArgumentException() {
        // Arrange
        TripServiceImpl service = new TripServiceImpl(
                mock(TripRepository.class),
                mock(TripStopRepository.class),
                mock(TripEventPublisher.class)
        );

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> service.createTrip("user-1", null));
        assertThrows(IllegalArgumentException.class, () -> service.createTrip("user-1", new CreateTripRequest(" ", "title")));
        assertThrows(IllegalArgumentException.class, () -> service.createTrip("user-1", new CreateTripRequest("route", " ")));
    }

    @Test
    void getTrip_shouldReturnTripWithSortedStops() {
        // Arrange
        TripRepository tripRepository = mock(TripRepository.class);
        TripStopRepository tripStopRepository = mock(TripStopRepository.class);
        TripEventPublisher eventPublisher = mock(TripEventPublisher.class);

        TripServiceImpl service = new TripServiceImpl(tripRepository, tripStopRepository, eventPublisher);

        UUID tripId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        Instant createdAt = Instant.parse("2025-01-01T00:00:00Z");
        Trip trip = Trip.restore(tripId, "user-1", "route-1", "Title", createdAt);

        TripStop stop2 = TripStop.restore(
                "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb",
                tripId.toString(),
                "place-2",
                2,
                Instant.parse("2025-01-01T02:00:00Z"),
                15
        );

        TripStop stop1 = TripStop.restore(
                "cccccccc-cccc-cccc-cccc-cccccccccccc",
                tripId.toString(),
                "place-1",
                1,
                null,
                null
        );

        when(tripRepository.findByIdAndUserId(tripId.toString(), "user-1")).thenReturn(Optional.of(trip));
        when(tripStopRepository.findByTripIdAndUserId(tripId.toString(), "user-1")).thenReturn(List.of(stop2, stop1));

        // Act
        TripResponse response = service.getTrip("user-1", tripId.toString());

        // Assert
        assertEquals(tripId.toString(), response.getId());
        assertEquals("user-1", response.getUserId());
        assertEquals("route-1", response.getRouteId());
        assertEquals("Title", response.getTitle());
        assertEquals(createdAt, response.getCreatedAt());

        assertEquals(2, response.getStops().size());
        assertEquals(1, response.getStops().get(0).getOrderIndex());
        assertEquals("place-1", response.getStops().get(0).getPlaceId());
        assertEquals(2, response.getStops().get(1).getOrderIndex());
        assertEquals("place-2", response.getStops().get(1).getPlaceId());

        verifyNoInteractions(eventPublisher);
    }

    @Test
    void getTrip_whenNotFound_shouldThrowTripNotFoundException() {
        // Arrange
        TripRepository tripRepository = mock(TripRepository.class);
        TripServiceImpl service = new TripServiceImpl(tripRepository, mock(TripStopRepository.class), mock(TripEventPublisher.class));

        when(tripRepository.findByIdAndUserId("trip-1", "user-1")).thenReturn(Optional.empty());

        // Act / Assert
        assertThrows(TripNotFoundException.class, () -> service.getTrip("user-1", "trip-1"));
    }

    @Test
    void updateStops_shouldRemoveAddPublishAndReturnResponse() {
        // Arrange
        TripRepository tripRepository = mock(TripRepository.class);
        TripStopRepository tripStopRepository = mock(TripStopRepository.class);
        TripEventPublisher eventPublisher = mock(TripEventPublisher.class);

        TripServiceImpl service = new TripServiceImpl(tripRepository, tripStopRepository, eventPublisher);

        UUID tripId = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");
        Trip trip = Trip.restore(tripId, "user-1", "route-1", "Title", Instant.parse("2025-01-01T00:00:00Z"));

        when(tripRepository.findByIdAndUserId(tripId.toString(), "user-1")).thenReturn(Optional.of(trip));

        List<String> removeIds = List.of(
                "eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee",
                "ffffffff-ffff-ffff-ffff-ffffffffffff"
        );

        TripStopDto add1 = new TripStopDto(null, null, "  place-1  ", 0, Instant.parse("2025-01-02T10:00:00Z"), 20);
        TripStopDto add2 = new TripStopDto(null, null, "place-2", 1, null, null);

        UpdateTripStopsRequest request = new UpdateTripStopsRequest(List.of(add1, add2), removeIds);

        when(tripStopRepository.findByTripIdAndUserId(tripId.toString(), "user-1")).thenReturn(List.of());

        ArgumentCaptor<List<TripStop>> savedStopsCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<String>> addedIdsCaptor = ArgumentCaptor.forClass(List.class);

        // Act
        TripResponse response = service.updateStops("user-1", tripId.toString(), request);

        // Assert
        verify(tripStopRepository).deleteByTripIdAndIds(tripId.toString(), removeIds);
        verify(eventPublisher).publishTripStopsRemoved(tripId.toString(), "user-1", removeIds);

        verify(tripStopRepository).saveAll(savedStopsCaptor.capture());
        List<TripStop> savedStops = savedStopsCaptor.getValue();
        assertEquals(2, savedStops.size());

        verify(eventPublisher).publishTripStopsAdded(eq(tripId.toString()), eq("user-1"), addedIdsCaptor.capture());
        List<String> addedIds = addedIdsCaptor.getValue();
        assertEquals(2, addedIds.size());
        assertTrue(addedIds.stream().allMatch(id -> id != null && !id.isBlank()));

        assertEquals(tripId.toString(), response.getId());
        assertNotNull(response.getStops());
    }

    @Test
    void updateStops_whenTripNotFound_shouldThrowTripNotFoundException() {
        // Arrange
        TripRepository tripRepository = mock(TripRepository.class);
        TripServiceImpl service = new TripServiceImpl(tripRepository, mock(TripStopRepository.class), mock(TripEventPublisher.class));

        when(tripRepository.findByIdAndUserId("trip-1", "user-1")).thenReturn(Optional.empty());

        // Act / Assert
        assertThrows(TripNotFoundException.class,
                () -> service.updateStops("user-1", "trip-1", new UpdateTripStopsRequest(List.of(), List.of())));
    }

    @Test
    void updateStops_whenRequestIsNull_shouldThrowNullPointerException() {
        // Arrange
        TripRepository tripRepository = mock(TripRepository.class);
        TripStopRepository tripStopRepository = mock(TripStopRepository.class);
        TripEventPublisher eventPublisher = mock(TripEventPublisher.class);

        TripServiceImpl service = new TripServiceImpl(tripRepository, tripStopRepository, eventPublisher);

        UUID tripId = UUID.fromString("12121212-1212-1212-1212-121212121212");
        Trip trip = Trip.restore(tripId, "user-1", "route-1", "Title", Instant.parse("2025-01-01T00:00:00Z"));

        when(tripRepository.findByIdAndUserId(tripId.toString(), "user-1")).thenReturn(Optional.of(trip));

        // Act / Assert
        assertThrows(NullPointerException.class, () -> service.updateStops("user-1", tripId.toString(), null));
        verifyNoInteractions(eventPublisher);
    }
}
