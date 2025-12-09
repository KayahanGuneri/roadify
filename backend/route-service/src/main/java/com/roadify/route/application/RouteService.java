package com.roadify.route.application;

import com.roadify.route.domain.Route;
import com.roadify.route.domain.RouteRepository;
import com.roadify.route.infrastructure.kafka.RouteEventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Application service responsible for orchestrating route preview and retrieval.
 */
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

    /**
     * Preview a route between two coordinates.
     * Tries Redis cache, but if Redis is faulty, the endpoint NEVER fires.
     */
    public Route previewRoute(double fromLat,
                              double fromLng,
                              double toLat,
                              double toLng) {

        String cacheKey = buildCacheKey(fromLat, fromLng, toLat, toLng);

        // 1) Safe cache GET
        Route cached = safeGetFromCache(cacheKey);
        if (cached != null) {
            log.info("Returning route from Redis cache. key={}", cacheKey);
            return cached;
        }

        // 2) Compute via ORS
        Route computed = orsClient.computeRoute(fromLat, fromLng, toLat, toLng);

        // 3) Persist in DB
        routeRepository.save(computed);

        // 4) Safe cache PUT
        safePutToCache(cacheKey, computed);

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

    /**
     * Redis down / timeout olursa exception'ı yutar, null döner.
     */
    private Route safeGetFromCache(String key) {
        try {
            return routeRedisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.warn("Failed to get route from Redis cache, key={}. Proceeding without cache.", key, e);
            return null;
        }
    }

    /**
     * Redis down / timeout olursa sadece log yazar, devam eder.
     */
    private void safePutToCache(String key, Route route) {
        try {
            routeRedisTemplate
                    .opsForValue()
                    .set(key, route, CACHE_TTL);
        } catch (Exception e) {
            log.warn("Failed to put route into Redis cache, key={}. Proceeding without cache.", key, e);
        }
    }
}
