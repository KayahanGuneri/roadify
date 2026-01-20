package com.roadify.analytics.infrastructure.repository.jdbc;

import com.roadify.analytics.domain.model.PopularPlaceCategoryStat;
import com.roadify.analytics.domain.repository.PopularPlaceCategoriesRepository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * JDBC implementation of PopularPlaceCategoriesRepository.
 *
 * Türkçe Özet:
 * popular_place_categories tablosu için JDBC tabanlı repository implementasyonu.
 */
@Repository
public class JdbcPopularPlaceCategoriesRepository implements PopularPlaceCategoriesRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcPopularPlaceCategoriesRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void incrementCategoryCount(LocalDate date, String category, int incrementBy) {
        String sql = """
                INSERT INTO popular_place_categories (usage_date, category, count)
                VALUES (:usage_date, :category, :increment_by)
                ON CONFLICT (usage_date, category)
                DO UPDATE SET count = popular_place_categories.count + :increment_by
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("usage_date", date)
                .addValue("category", category)
                .addValue("increment_by", incrementBy);

        jdbcTemplate.update(sql, params);
    }

    @Override
    public List<PopularPlaceCategoryStat> findTopCategories(LocalDate from, LocalDate to, int limit) {
        String sql = """
                SELECT usage_date, category, count
                FROM popular_place_categories
                WHERE usage_date BETWEEN :from AND :to
                ORDER BY count DESC
                LIMIT :limit
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("from", from)
                .addValue("to", to)
                .addValue("limit", limit);

        return jdbcTemplate.query(sql, params, new PopularPlaceCategoryRowMapper());
    }

    private static class PopularPlaceCategoryRowMapper implements RowMapper<PopularPlaceCategoryStat> {
        @Override
        public PopularPlaceCategoryStat mapRow(ResultSet rs, int rowNum) throws SQLException {
            LocalDate date = rs.getObject("usage_date", LocalDate.class);
            String category = rs.getString("category");
            int count = rs.getInt("count");
            return new PopularPlaceCategoryStat(date, category, count);
        }
    }
}
