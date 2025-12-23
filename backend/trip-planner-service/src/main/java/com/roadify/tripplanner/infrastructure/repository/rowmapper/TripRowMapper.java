package com.roadify.tripplanner.infrastructure.repository.rowmapper;

import com.roadify.tripplanner.domain.Trip;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

public class TripRowMapper implements RowMapper<Trip> {

    @Override
    public Trip mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Trip.restore(
                rs.getObject("id", UUID.class),
                rs.getString("user_id"),
                rs.getString("route_id"),
                rs.getString("title"),
                rs.getTimestamp("created_at").toInstant()
        );
    }
}
