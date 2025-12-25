package com.roadify.tripplanner.api;

import com.roadify.tripplanner.api.dto.CreateTripRequest;
import com.roadify.tripplanner.api.dto.TripResponse;
import com.roadify.tripplanner.api.dto.UpdateTripStopsRequest;
import com.roadify.tripplanner.application.service.TripServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TripControllerTest {

    @Test
    void createTrip_shouldReturn201Created() {
        // Arrange
        TripServiceImpl service = mock(TripServiceImpl.class);
        TripController controller = new TripController(service);

        CreateTripRequest request = new CreateTripRequest("route-1", "Title");
        TripResponse expected = new TripResponse("trip-1", "user-1", "route-1", "Title", Instant.now(), List.of());

        when(service.createTrip("user-1", request)).thenReturn(expected);

        // Act
        ResponseEntity<TripResponse> response = controller.createTrip("user-1", request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(expected, response.getBody());
        verify(service).createTrip("user-1", request);
        verifyNoMoreInteractions(service);
    }

    @Test
    void getTrip_shouldReturn200Ok() {
        // Arrange
        TripServiceImpl service = mock(TripServiceImpl.class);
        TripController controller = new TripController(service);

        TripResponse expected = new TripResponse("trip-1", "user-1", "route-1", "Title", Instant.now(), List.of());
        when(service.getTrip("user-1", "trip-1")).thenReturn(expected);

        // Act
        ResponseEntity<TripResponse> response = controller.getTrip("user-1", "trip-1");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(expected, response.getBody());
        verify(service).getTrip("user-1", "trip-1");
        verifyNoMoreInteractions(service);
    }

    @Test
    void updateStops_shouldReturn200Ok() {
        // Arrange
        TripServiceImpl service = mock(TripServiceImpl.class);
        TripController controller = new TripController(service);

        UpdateTripStopsRequest request = new UpdateTripStopsRequest(List.of(), List.of());
        TripResponse expected = new TripResponse("trip-1", "user-1", "route-1", "Title", Instant.now(), List.of());

        when(service.updateStops("user-1", "trip-1", request)).thenReturn(expected);

        // Act
        ResponseEntity<TripResponse> response = controller.updateStops("user-1", "trip-1", request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(expected, response.getBody());
        verify(service).updateStops("user-1", "trip-1", request);
        verifyNoMoreInteractions(service);
    }
}
