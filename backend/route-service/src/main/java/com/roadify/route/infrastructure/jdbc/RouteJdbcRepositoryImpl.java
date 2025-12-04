package com.roadify.route.infrastructure.jdbc;

import com.roadify.route.domain.Route;
import com.roadify.route.domain.RouteRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

/**
 * JDBC-based implementation of RouteRepository using Spring's JdbcTemplate.
 */
@Repository
public class RouteJdbcRepositoryImpl implements RouteRepository {

    private static final String INSERT_SQL = """
            INSERT INTO route (
                id,
                from_lat,
                from_lng,
                to_lat,
                to_lng,
                distance_km,
                duration_minutes,
                geometry
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String SELECT_BY_ID_SQL = """
            SELECT
                id,
                from_lat,
                from_lng,
                to_lat,
                to_lng,
                distance_km,
                duration_minutes,
                geometry
            FROM route
            WHERE id = ?
            """;

    private final JdbcTemplate jdbcTemplate;

    public RouteJdbcRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(Route route) {
        jdbcTemplate.update(
                INSERT_SQL,
                route.getId(),
                route.getFromLat(),
                route.getFromLng(),
                route.getToLat(),
                route.getToLng(),
                route.getDistanceKm(),
                route.getDurationMinutes(),
                route.getGeometry()
        );
    }

    @Override
    public Optional<Route> findById(String id) {
        return jdbcTemplate
                .query(SELECT_BY_ID_SQL, new Object[]{id}, routeRowMapper())
                .stream()
                .findFirst();
    }

    private RowMapper<Route> routeRowMapper() {
        return new RowMapper<>() {
            @Override
            public Route mapRow(ResultSet rs, int rowNum) throws SQLException {
                return Route.builder()
                        .id(UUID.fromString(rs.getString("id")))
                        .fromLat(rs.getDouble("from_lat"))
                        .fromLng(rs.getDouble("from_lng"))
                        .toLat(rs.getDouble("to_lat"))
                        .toLng(rs.getDouble("to_lng"))
                        .distanceKm(rs.getDouble("distance_km"))
                        .durationMinutes(rs.getDouble("duration_minutes"))
                        .geometry(rs.getString("geometry"))
                        .build();
            }
        };
    }
}
