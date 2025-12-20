package com.roadify.tripplanner.infrastructure.repository;

import com.roadify.tripplanner.application.port.TripStopRepository;
import com.roadify.tripplanner.domain.TripStop;
import com.roadify.tripplanner.infrastructure.repository.rowmapper.TripStopRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JDBC implementation of TripStopRepository.
 */
@Repository
public class JdbcTripStopRepository implements TripStopRepository {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final TripStopRowMapper rowMapper = new TripStopRowMapper();

    public JdbcTripStopRepository(
            JdbcTemplate jdbcTemplate,
            NamedParameterJdbcTemplate namedParameterJdbcTemplate
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public void saveAll(List<TripStop> stops) {
        if (stops == null || stops.isEmpty()) {
            return;
        }

        String sql = """
            INSERT INTO trip_stops
            (id, trip_id, place_id, order_index, planned_arrival_time, planned_duration_minutes)
            VALUES
            (:id, :tripId, :placeId, :orderIndex, :plannedArrivalTime, :plannedDurationMinutes)
            """;

        List<Map<String, Object>> batchValues = stops.stream()
                .map(stop -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", stop.getId());
                    map.put("tripId", stop.getTripId());
                    map.put("placeId", stop.getPlaceId());
                    map.put("orderIndex", stop.getOrderIndex());
                    map.put("plannedArrivalTime", stop.getPlannedArrivalTime().orElse(null));
                    map.put("plannedDurationMinutes", stop.getPlannedDurationMinutes().orElse(null));
                    return map;
                })
                .toList();

        namedParameterJdbcTemplate.batchUpdate(sql, batchValues.toArray(new Map[0]));
    }

    @Override
    public List<TripStop> findByTripIdAndUserId(String tripId, String userId) {
        String sql = """
            SELECT s.*
            FROM trip_stops s
            JOIN trips t ON t.id = s.trip_id
            WHERE s.trip_id = ? AND t.user_id = ?
            ORDER BY s.order_index
            """;

        return jdbcTemplate.query(sql, rowMapper, tripId, userId);
    }

    @Override
    public int deleteByTripIdAndIds(String tripId, List<String> stopIds) {
        if (stopIds == null || stopIds.isEmpty()) {
            return 0;
        }

        String sql = """
            DELETE FROM trip_stops
            WHERE trip_id = :tripId
              AND id IN (:ids)
            """;

        Map<String, Object> params = new HashMap<>();
        params.put("tripId", tripId);
        params.put("ids", stopIds);

        return namedParameterJdbcTemplate.update(sql, params);
    }
}
