package com.roadify.tripplanner.api.error;

import com.roadify.tripplanner.application.exception.TripNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ApiExceptionHandlerTest {

    @Test
    void handleTripNotFound_shouldReturn404AndBody() {
        // Arrange
        ApiExceptionHandler handler = new ApiExceptionHandler();
        TripNotFoundException ex = new TripNotFoundException("trip-1");

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleTripNotFound(ex);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().get("status"));
        assertEquals(ex.getMessage(), response.getBody().get("message"));
        assertNotNull(response.getBody().get("timestamp"));
        assertDoesNotThrow(() -> Instant.parse(response.getBody().get("timestamp").toString()));
    }

    @Test
    void handleBadRequest_shouldReturn400AndBody() {
        // Arrange
        ApiExceptionHandler handler = new ApiExceptionHandler();
        IllegalArgumentException ex = new IllegalArgumentException("bad input");

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleBadRequest(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().get("status"));
        assertEquals("bad input", response.getBody().get("message"));
        assertNotNull(response.getBody().get("timestamp"));
        assertDoesNotThrow(() -> Instant.parse(response.getBody().get("timestamp").toString()));
    }
}
