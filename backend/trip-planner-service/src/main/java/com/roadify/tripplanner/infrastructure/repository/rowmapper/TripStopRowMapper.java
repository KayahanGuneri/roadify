package com.roadify.tripplanner.infrastructure.repository.rowmapper;

import com.roadify.tripplanner.domain.TripStop;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

/**
 * Maps a DB row to TripStop domain object.
 */
public class TripStopRowMapper implements RowMapper<TripStop> {

    @Override
    public TripStop mapRow(ResultSet rs, int rowNum) throws SQLException {
        return TripStop.restore(
                rs.getString("id"),
                rs.getString("trip_id"),
                rs.getString("place_id"),
                rs.getInt("order_index"),
                rs.getTimestamp("planned_arrival_time") != null
                        ? rs.getTimestamp("planned_arrival_time").toInstant()
                        : null,
                rs.getObject("planned_duration_minutes", Integer.class)
        );
    }
}
