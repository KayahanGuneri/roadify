package com.roadify.route.application;

import com.roadify.route.domain.Route;
import com.roadify.route.domain.RouteRepository;
import com.roadify.route.infrastructure.kafka.RouteEventProducer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Application service responsible for orchestrating route preview and retrieval.
 */
@Service
public class RouteService {

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

    /**
     * Preview a route between two coordinates. Uses Redis cache first, falls back to ORS + DB.
     */
    public Route previewRoute(double fromLat,
                              double fromLng,
                              double toLat,
                              double toLng) {

        String cacheKey = buildCacheKey(fromLat, fromLng, toLat, toLng);

        // 1) Check cache
        Route cached = routeRedisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }

        // 2) Compute via ORS
        Route computed = orsClient.computeRoute(fromLat, fromLng, toLat, toLng);

        // 3) Persist in DB
        routeRepository.save(computed);

        // 4) Store in cache with TTL
        routeRedisTemplate
                .opsForValue()
                .set(cacheKey, computed, CACHE_TTL);

        // 5) Publish domain event to Kafka
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
}
