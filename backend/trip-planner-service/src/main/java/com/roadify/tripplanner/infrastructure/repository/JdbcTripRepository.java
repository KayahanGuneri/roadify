package com.roadify.tripplanner.infrastructure.repository;

import com.roadify.tripplanner.application.port.TripRepository;
import com.roadify.tripplanner.domain.Trip;
import com.roadify.tripplanner.infrastructure.repository.rowmapper.TripRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

/**
 * JDBC implementation of TripRepository.
 */
@Repository
public class JdbcTripRepository implements TripRepository {

    private final JdbcTemplate jdbcTemplate;
    private final TripRowMapper tripRowMapper = new TripRowMapper();

    public JdbcTripRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(Trip trip) {
        String sql = """
            INSERT INTO trips (id, user_id, route_id, title, created_at)
            VALUES (?, ?, ?, ?, ?)
            """;

        jdbcTemplate.update(
                sql,
                trip.getId(),
                trip.getUserId(),
                trip.getRouteId(),
                trip.getTitle(),
                Timestamp.from(trip.getCreatedAt())
        );
    }

    @Override
    public Optional<Trip> findByIdAndUserId(String id, String userId) {
        UUID tripId = UUID.fromString(id);

        String sql = """
        SELECT *
        FROM trips
        WHERE id = ? AND user_id = ?
        """;

        return jdbcTemplate.query(sql, tripRowMapper, tripId, userId)
                .stream()
                .findFirst();
    }
}
