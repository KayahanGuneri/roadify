package com.roadify.analytics.infrastructure.repository.jdbc;

import com.roadify.analytics.domain.model.AiUsageStat;
import com.roadify.analytics.domain.repository.AiUsageStatsRepository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * JDBC implementation of AiUsageStatsRepository.
 *
 * Türkçe Özet:
 * ai_usage_stats tablosu için JDBC tabanlı repository implementasyonu.
 */
@Repository
public class JdbcAiUsageStatsRepository implements AiUsageStatsRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcAiUsageStatsRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void incrementRequestCount(LocalDate date) {
        String sql = """
                INSERT INTO ai_usage_stats (usage_date, request_count, accepted_count)
                VALUES (:usage_date, 1, 0)
                ON CONFLICT (usage_date)
                DO UPDATE SET request_count = ai_usage_stats.request_count + 1
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("usage_date", date);

        jdbcTemplate.update(sql, params);
    }

    @Override
    public void incrementAcceptedCount(LocalDate date) {
        String sql = """
                INSERT INTO ai_usage_stats (usage_date, request_count, accepted_count)
                VALUES (:usage_date, 0, 1)
                ON CONFLICT (usage_date)
                DO UPDATE SET accepted_count = ai_usage_stats.accepted_count + 1
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("usage_date", date);

        jdbcTemplate.update(sql, params);
    }

    @Override
    public List<AiUsageStat> findBetween(LocalDate from, LocalDate to) {
        String sql = """
                SELECT usage_date, request_count, accepted_count
                FROM ai_usage_stats
                WHERE usage_date BETWEEN :from AND :to
                ORDER BY usage_date
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("from", from)
                .addValue("to", to);

        return jdbcTemplate.query(sql, params, new AiUsageStatRowMapper());
    }

    private static class AiUsageStatRowMapper implements RowMapper<AiUsageStat> {
        @Override
        public AiUsageStat mapRow(ResultSet rs, int rowNum) throws SQLException {
            LocalDate date = rs.getObject("usage_date", LocalDate.class);
            int requestCount = rs.getInt("request_count");
            int acceptedCount = rs.getInt("accepted_count");
            return new AiUsageStat(date, requestCount, acceptedCount);
        }
    }
}
