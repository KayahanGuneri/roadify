package com.roadify.route.infrastructure.jdbc;

import com.roadify.route.RouteServiceApplication;
import com.roadify.route.domain.Route;
import com.roadify.route.domain.RouteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for RouteJdbcRepositoryImpl using Testcontainers + PostgreSQL.
 */
@SpringJUnitConfig
@SpringBootTest(
        classes = RouteServiceApplication.class,
        properties = {
                "spring.cloud.config.enabled=false",
                "spring.cloud.config.import-check.enabled=false",
                "eureka.client.enabled=false"
        }
)
@Testcontainers
class RouteJdbcRepositoryImplIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("roadify")
            .withUsername("roadify")
            .withPassword("roadify");

    @DynamicPropertySource
    static void configureDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void saveAndFindById_shouldPersistAndLoadRoute() {
        // Ensure table exists in the test database
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS route (
                    id               VARCHAR(36) PRIMARY KEY,
                    from_lat         DOUBLE PRECISION NOT NULL,
                    from_lng         DOUBLE PRECISION NOT NULL,
                    to_lat           DOUBLE PRECISION NOT NULL,
                    to_lng           DOUBLE PRECISION NOT NULL,
                    distance_km      DOUBLE PRECISION NOT NULL,
                    duration_minutes DOUBLE PRECISION NOT NULL,
                    geometry         TEXT NOT NULL
                )
                """);

        // given
        String id = UUID.randomUUID().toString();

        Route route = Route.builder()
                .id(UUID.fromString(id))
                .fromLat(36.8841)
                .fromLng(30.7056)
                .toLat(41.0082)
                .toLng(28.9784)
                .distanceKm(700.5)
                .durationMinutes(480.0)
                .geometry("encoded-polyline-placeholder")
                .build();

        // when
        routeRepository.save(route);
        Optional<Route> loaded = routeRepository.findById(id);

        // then
        assertThat(loaded).isPresent();
        assertThat(loaded.get().getId()).isEqualTo(id);
        assertThat(loaded.get().getDistanceKm()).isEqualTo(700.5);
        assertThat(loaded.get().getGeometry()).isEqualTo("encoded-polyline-placeholder");
    }
}
