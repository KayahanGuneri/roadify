package com.roadify.tripplanner.infrastructure.repository.rowmapper;

import com.roadify.tripplanner.domain.TripStop;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TripStopRowMapperTest {

    @Test
    void mapRow_whenArrivalPresent_shouldMap() throws Exception {
        // Arrange
        ResultSet rs = mock(ResultSet.class);

        UUID id = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        UUID tripId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
        Instant arrival = Instant.parse("2025-01-01T10:00:00Z");

        when(rs.getObject("id", UUID.class)).thenReturn(id);
        when(rs.getObject("trip_id", UUID.class)).thenReturn(tripId);
        when(rs.getTimestamp("planned_arrival_time")).thenReturn(Timestamp.from(arrival));
        when(rs.getString("place_id")).thenReturn("place-1");
        when(rs.getInt("order_index")).thenReturn(1);
        when(rs.getObject("planned_duration_minutes", Integer.class)).thenReturn(30);

        TripStopRowMapper mapper = new TripStopRowMapper();

        // Act
        TripStop stop = mapper.mapRow(rs, 0);

        // Assert
        assertEquals(id.toString(), stop.getId());
        assertEquals(tripId.toString(), stop.getTripId());
        assertEquals("place-1", stop.getPlaceId());
        assertEquals(1, stop.getOrderIndex());
        assertEquals(arrival, stop.getPlannedArrivalTime().orElseThrow());
        assertEquals(30, stop.getPlannedDurationMinutes().orElseThrow());
    }

    @Test
    void mapRow_whenArrivalNull_shouldMapWithEmptyOptionals() throws Exception {
        // Arrange
        ResultSet rs = mock(ResultSet.class);

        UUID id = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
        UUID tripId = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");

        when(rs.getObject("id", UUID.class)).thenReturn(id);
        when(rs.getObject("trip_id", UUID.class)).thenReturn(tripId);
        when(rs.getTimestamp("planned_arrival_time")).thenReturn(null);
        when(rs.getString("place_id")).thenReturn("place-2");
        when(rs.getInt("order_index")).thenReturn(0);
        when(rs.getObject("planned_duration_minutes", Integer.class)).thenReturn(null);

        TripStopRowMapper mapper = new TripStopRowMapper();

        // Act
        TripStop stop = mapper.mapRow(rs, 0);

        // Assert
        assertEquals(id.toString(), stop.getId());
        assertEquals(tripId.toString(), stop.getTripId());
        assertEquals("place-2", stop.getPlaceId());
        assertEquals(0, stop.getOrderIndex());
        assertTrue(stop.getPlannedArrivalTime().isEmpty());
        assertTrue(stop.getPlannedDurationMinutes().isEmpty());
    }
}
