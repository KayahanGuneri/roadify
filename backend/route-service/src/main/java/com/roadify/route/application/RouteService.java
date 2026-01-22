package com.roadify.route.application;

import com.roadify.route.domain.Route;
import com.roadify.route.domain.RouteRepository;
import com.roadify.route.infrastructure.kafka.RouteEventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
public class RouteService {

    private static final Logger log = LoggerFactory.getLogger(RouteService.class);
    private static final Duration CACHE_TTL = Duration.ofHours(6);

    private final RouteRepository routeRepository;
    private final OrsClient orsClient;
    private final RedisTemplate<String, Route> routeRedisTemplate;
    private final RouteEventProducer routeEventProducer;

    public RouteService(RouteRepository routeRepository,
                        OrsClient orsClient,
                        RedisTemplate<String, Route> routeRedisTemplate,
                        RouteEventProducer routeEventProducer) {
        this.routeRepository = routeRepository;
        this.orsClient = orsClient;
        this.routeRedisTemplate = routeRedisTemplate;
        this.routeEventProducer = routeEventProducer;
    }

    public Route previewRoute(double fromLat,
                              double fromLng,
                              double toLat,
                              double toLng) {

        String cacheKey = buildCacheKey(fromLat, fromLng, toLat, toLng);

        Route cached = safeGetFromCache(cacheKey);
        if (cached != null) {
            log.info("Returning route from Redis cache. key={}", cacheKey);
            return cached;
        }

        Route computed;
        try {
            computed = orsClient.computeRoute(fromLat, fromLng, toLat, toLng);
        } catch (Exception ex) {
            // ORS down/blocked vs. durumlarda analytics'i yine doldurabilmek için fallback
            log.warn("ORS failed. Falling back to a minimal route. from=({}, {}), to=({}, {}). reason={}",
                    fromLat, fromLng, toLat, toLng, ex.getMessage());

            computed = Route.builder()
                    .id(UUID.randomUUID())
                    .fromLat(fromLat)
                    .fromLng(fromLng)
                    .toLat(toLat)
                    .toLng(toLng)
                    .distanceKm(0.0)
                    .durationMinutes(0.0)
                    .geometry(null)
                    .build();
        }

        routeRepository.save(computed);
        safePutToCache(cacheKey, computed);

        // Kafka event (analytics buradan dolacak)
        routeEventProducer.sendRouteCreated(computed);

        return computed;
    }

    public Route getRouteById(String id) {
        return routeRepository
                .findById(id)
                .orElseThrow(() -> new RouteNotFoundException(id));
    }

    private String buildCacheKey(double fromLat,
                                 double fromLng,
                                 double toLat,
                                 double toLng) {
        return "route:" + fromLat + ":" + fromLng + ":" + toLat + ":" + toLng;
    }

    private Route safeGetFromCache(String key) {
        try {
            return routeRedisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.warn("Failed to get route from Redis cache, key={}. Proceeding without cache.", key, e);
            return null;
        }
    }

    private void safePutToCache(String key, Route route) {
        try {
            routeRedisTemplate.opsForValue().set(key, route, CACHE_TTL);
        } catch (Exception e) {
            log.warn("Failed to put route into Redis cache, key={}. Proceeding without cache.", key, e);
        }
    }
}
