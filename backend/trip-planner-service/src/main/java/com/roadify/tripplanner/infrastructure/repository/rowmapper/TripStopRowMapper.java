package com.roadify.tripplanner.infrastructure.repository.rowmapper;

import com.roadify.tripplanner.domain.TripStop;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

public class TripStopRowMapper implements RowMapper<TripStop> {

    @Override
    public TripStop mapRow(ResultSet rs, int rowNum) throws SQLException {

        UUID id = rs.getObject("id", UUID.class);
        UUID tripId = rs.getObject("trip_id", UUID.class);

        Timestamp arrivalTs = rs.getTimestamp("planned_arrival_time");

        return TripStop.restore(
                id.toString(),
                tripId.toString(),
                rs.getString("place_id"),
                rs.getString("place_name"), // NEW
                rs.getInt("order_index"),
                arrivalTs != null ? arrivalTs.toInstant() : null,
                rs.getObject("planned_duration_minutes", Integer.class)
        );
    }
}
