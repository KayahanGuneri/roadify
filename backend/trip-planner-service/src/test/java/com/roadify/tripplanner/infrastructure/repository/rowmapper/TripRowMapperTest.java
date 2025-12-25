package com.roadify.tripplanner.infrastructure.repository.rowmapper;

import com.roadify.tripplanner.domain.Trip;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TripRowMapperTest {

    @Test
    void mapRow_shouldReturnTrip() throws Exception {
        // Arrange
        ResultSet rs = mock(ResultSet.class);

        UUID id = UUID.fromString("11111111-2222-3333-4444-555555555555");
        Instant createdAt = Instant.parse("2025-01-01T00:00:00Z");

        when(rs.getObject("id", UUID.class)).thenReturn(id);
        when(rs.getString("user_id")).thenReturn("u1");
        when(rs.getString("route_id")).thenReturn("r1");
        when(rs.getString("title")).thenReturn("t1");
        when(rs.getTimestamp("created_at")).thenReturn(Timestamp.from(createdAt));

        TripRowMapper mapper = new TripRowMapper();

        // Act
        Trip trip = mapper.mapRow(rs, 0);

        // Assert
        assertEquals(id, trip.getId());
        assertEquals("u1", trip.getUserId());
        assertEquals("r1", trip.getRouteId());
        assertEquals("t1", trip.getTitle());
        assertEquals(createdAt, trip.getCreatedAt());
    }
}
