package com.roadify.places.application;

import com.roadify.places.domain.Place;
import com.roadify.places.domain.PlaceCategory;
import com.roadify.places.infrastructure.kafka.PlacesFetchedEvent;
import com.roadify.places.infrastructure.kafka.PlacesFetchedEventProducer;
import com.roadify.places.infrastructure.provider.PlaceNormalizer;
import com.roadify.places.infrastructure.provider.RawPlace;
import com.roadify.places.infrastructure.route.RouteSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Orchestrates fetching, normalizing, caching and filtering places for a route.
 * - Reads route geometry from route-service
 * - Calls external providers (Geoapify, Overpass) to fetch raw places
 * - Normalizes provider-specific data into the domain Place model
 * - Optionally caches results in Redis
 * - Applies filtering (category, rating, detour, paging)
 * - Publishes a PlacesFetchedEvent for analytics
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PlacesService {

    private final RouteServiceClient routeServiceClient;
    private final GeoapifyClient geoapifyClient;
    private final OverpassClient overpassClient;
    private final PlaceNormalizer placeNormalizer;
    private final RedisTemplate<String, Place[]> placesRedisTemplate;
    private final ObjectProvider<PlacesFetchedEventProducer> eventProducerProvider;

    /**
     * Main entry point used by the API layer.
     * 1) Try to read places from Redis cache (by routeId + filter criteria)
     * 2) If cache is empty or missing, call external providers and normalize
     * 3) Cache non-empty results
     * 4) Apply filtering (category, rating, detour, offset/limit)
     */
    public List<Place> getPlacesForRoute(String routeId, PlaceFilterCriteria criteria) {

        String cacheKey = buildCacheKey(routeId, criteria);

        // 1) Try to read from cache
        Place[] cached = placesRedisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            if (cached.length > 0) {
                // Cache hit with non-empty list: we can safely return filtered version
                log.info("[PlacesService] Cache hit. key={}, size={}", cacheKey, cached.length);
                return applyFilter(Arrays.asList(cached), criteria);
            } else {
                // Cache contains an empty list (from older behavior).
                // We log it but will still recompute, so we can repopulate if providers start returning data.
                log.info("[PlacesService] Cache hit with EMPTY list. key={}. Will re-fetch from providers.", cacheKey);
            }
        } else {
            log.info("[PlacesService] Cache miss. key={}", cacheKey);
        }

        // 2) Fetch route geometry from route-service
        RouteSummary route = routeServiceClient.getRouteById(routeId);
        String geometry = route.getGeometry();

        // 3) Fetch raw places from providers (each wrapped in safe helpers)
        List<RawPlace> rawPlaces = new ArrayList<>();

        List<RawPlace> fromGeoapify = fetchFromGeoapifySafely(geometry);
        log.info("[PlacesService] Geoapify returned {} raw places", fromGeoapify.size());
        rawPlaces.addAll(fromGeoapify);

        List<RawPlace> fromOverpass = fetchFromOverpassSafely(geometry);
        log.info("[PlacesService] Overpass returned {} raw places", fromOverpass.size());
        rawPlaces.addAll(fromOverpass);

        log.info("[PlacesService] Total raw places from providers = {}", rawPlaces.size());

        // 4) Normalize provider-specific data into our domain Place model
        List<Place> normalized = placeNormalizer.normalize(rawPlaces, geometry);
        log.info("[PlacesService] Normalized places count = {}", normalized.size());

        // 5) Cache only non-empty normalized results
        if (!normalized.isEmpty()) {
            placesRedisTemplate
                    .opsForValue()
                    .set(cacheKey, normalized.toArray(new Place[0]));
            log.info("[PlacesService] Cached {} places under key={}", normalized.size(), cacheKey);
        } else {
            log.info("[PlacesService] Normalized list is empty. Skipping cache write for key={}", cacheKey);
        }

        // 6) Publish domain event (for analytics, downstream consumers, etc.)
        publishEvent(routeId, normalized);

        // 7) Apply filter (category, rating, detour, paging) and return
        List<Place> filtered = applyFilter(normalized, criteria);
        log.info("[PlacesService] Filtered places count = {}", filtered.size());

        return filtered;
    }

    /**
     * Calls Geoapify client in a safe manner:
     * - Any exception is caught and logged
     * - Returns an empty list instead of throwing, so the service continues to work
     */
    private List<RawPlace> fetchFromGeoapifySafely(String geometry) {
        try {
            List<RawPlace> result = geoapifyClient.fetchPlaces(geometry);
            return (result != null) ? result : List.of();
        } catch (Exception e) {
            log.warn("[PlacesService] Geoapify fetch failed, continuing without Geoapify data.", e);
            return List.of();
        }
    }

    /**
     * Calls Overpass client in a safe manner:
     * - Handles any WebClient error
     * - Logs the error and returns an empty list instead of bubbling up
     */
    private List<RawPlace> fetchFromOverpassSafely(String geometry) {
        try {
            List<RawPlace> result = overpassClient.fetchPlaces(geometry);
            return (result != null) ? result : List.of();
        } catch (Exception e) {
            log.warn("[PlacesService] Overpass fetch failed, continuing without Overpass data.", e);
            return List.of();
        }
    }

    /**
     * Builds a cache key that uniquely identifies:
     * - the route
     * - the applied filter criteria (category, minRating, maxDetourKm)
     */
    private String buildCacheKey(String routeId, PlaceFilterCriteria criteria) {
        return "route:" + routeId + ":places:" +
                Objects.toString(criteria.getCategory(), "ALL") + ":" +
                Objects.toString(criteria.getMinRating(), "NR") + ":" +
                Objects.toString(criteria.getMaxDetourKm(), "ND");
    }

    /**
     * Applies business-level filtering on top of the normalized places:
     * - Filter by category (if provided)
     * - Filter by minimum rating (if provided)
     * - Filter by maximum detour distance (if provided)
     * - Apply pagination via offset & limit
     */
    private List<Place> applyFilter(List<Place> places, PlaceFilterCriteria criteria) {
        return places.stream()
                // Category filter: if criteria.category == null, accept all categories
                .filter(p -> criteria.getCategory() == null || p.getCategory() == criteria.getCategory())
                // Rating filter: if criteria.minRating == null, accept all ratings
                .filter(p -> criteria.getMinRating() == null ||
                        (p.getRating() != null && p.getRating() >= criteria.getMinRating()))
                // Detour filter: if criteria.maxDetourKm == null, accept all detours
                .filter(p -> criteria.getMaxDetourKm() == null || p.getDetourKm() <= criteria.getMaxDetourKm())
                // Offset == "how many elements to skip"
                .skip(criteria.getOffset() == null ? 0 : criteria.getOffset())
                // Limit == "maximum number of elements to return"
                .limit(criteria.getLimit() == null ? places.size() : criteria.getLimit())
                .collect(Collectors.toList());
    }

    /**
     * Publishes an event with some aggregated statistics:
     * - Total number of places
     * - Per-category counts
     *
     * If PlacesFetchedEventProducer is not configured in the context,
     * the method is a no-op.
     */
    private void publishEvent(String routeId, List<Place> places) {
        PlacesFetchedEventProducer producer = eventProducerProvider.getIfAvailable();

        if (producer == null) {
            log.debug("[PlacesService] PlacesFetchedEventProducer not available, skipping event publish.");
            return;
        }

        Map<PlaceCategory, Integer> categoryCounts = places.stream()
                .collect(Collectors.groupingBy(Place::getCategory, Collectors.summingInt(p -> 1)));

        PlacesFetchedEvent event = PlacesFetchedEvent.builder()
                .routeId(routeId)
                .totalCount(places.size())
                .categoryCounts(categoryCounts)
                .build();

        producer.publish(event);
        log.info("[PlacesService] Published PlacesFetchedEvent for routeId={}, totalCount={}",
                routeId, places.size());
    }
}
