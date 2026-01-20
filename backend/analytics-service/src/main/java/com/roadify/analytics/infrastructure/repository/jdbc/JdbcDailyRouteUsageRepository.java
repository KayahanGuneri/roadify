package com.roadify.analytics.infrastructure.repository.jdbc;

import com.roadify.analytics.domain.model.DailyRouteUsage;
import com.roadify.analytics.domain.repository.DailyRouteUsageRepository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * JDBC implementation of DailyRouteUsageRepository.
 *
 * Türkçe Özet:
 * daily_route_usage tablosu için JDBC tabanlı repository implementasyonu.
 */
@Repository
public class JdbcDailyRouteUsageRepository implements DailyRouteUsageRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcDailyRouteUsageRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void incrementRouteCount(LocalDate date) {
        // PostgreSQL upsert (INSERT ... ON CONFLICT)
        String sql = """
                INSERT INTO daily_route_usage (usage_date, route_count)
                VALUES (:usage_date, 1)
                ON CONFLICT (usage_date)
                DO UPDATE SET route_count = daily_route_usage.route_count + 1
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("usage_date", date);

        jdbcTemplate.update(sql, params);
    }

    @Override
    public List<DailyRouteUsage> findBetween(LocalDate from, LocalDate to) {
        String sql = """
                SELECT usage_date, route_count
                FROM daily_route_usage
                WHERE usage_date BETWEEN :from AND :to
                ORDER BY usage_date
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("from", from)
                .addValue("to", to);

        return jdbcTemplate.query(sql, params, new DailyRouteUsageRowMapper());
    }

    private static class DailyRouteUsageRowMapper implements RowMapper<DailyRouteUsage> {
        @Override
        public DailyRouteUsage mapRow(ResultSet rs, int rowNum) throws SQLException {
            LocalDate date = rs.getObject("usage_date", LocalDate.class);
            int count = rs.getInt("route_count");
            return new DailyRouteUsage(date, count);
        }
    }
}
