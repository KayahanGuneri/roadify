package com.roadify.places.application;

import com.roadify.places.domain.Place;
import com.roadify.places.domain.PlaceCategory;
import com.roadify.places.infrastructure.kafka.PlacesFetchedEvent;
import com.roadify.places.infrastructure.kafka.PlacesFetchedEventProducer;
import com.roadify.places.infrastructure.provider.PlaceNormalizer;
import com.roadify.places.infrastructure.provider.RawPlace;
import com.roadify.places.infrastructure.route.RouteSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Orchestrates fetching, normalizing, caching and filtering places for a route.
 */
@Service
@RequiredArgsConstructor
public class PlacesService {

    private final RouteServiceClient routeServiceClient;
    private final OpenTripMapClient openTripMapClient;
    private final OverpassClient overpassClient;
    private final PlaceNormalizer placeNormalizer; // you can later split per-provider
    private final RedisTemplate<String, Place[]> placesRedisTemplate;
    private final PlacesFetchedEventProducer eventProducer;

    public List<Place> getPlacesForRoute(String routeId, PlaceFilterCriteria criteria) {

        String cacheKey = buildCacheKey(routeId, criteria);

        Place[] cached = placesRedisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return applyFilter(Arrays.asList(cached), criteria);
        }

        RouteSummary route = routeServiceClient.getRouteById(routeId);

        List<RawPlace> rawPlaces = new ArrayList<>();
        rawPlaces.addAll(openTripMapClient.fetchPlaces(route.getGeometry()));
        rawPlaces.addAll(overpassClient.fetchPlaces(route.getGeometry()));

        List<Place> normalized = placeNormalizer.normalize(rawPlaces, route.getGeometry());
        List<Place> filtered = applyFilter(normalized, criteria);

        placesRedisTemplate.opsForValue().set(cacheKey, normalized.toArray(new Place[0]));

        publishEvent(routeId, normalized);

        return filtered;
    }

    private String buildCacheKey(String routeId, PlaceFilterCriteria criteria) {
        return "route:" + routeId + ":places:" +
                Objects.toString(criteria.getCategory(), "ALL") + ":" +
                Objects.toString(criteria.getMinRating(), "NR") + ":" +
                Objects.toString(criteria.getMaxDetourKm(), "ND");
    }

    private List<Place> applyFilter(List<Place> places, PlaceFilterCriteria criteria) {
        return places.stream()
                .filter(p -> criteria.getCategory() == null || p.getCategory() == criteria.getCategory())
                .filter(p -> criteria.getMinRating() == null || (p.getRating() != null && p.getRating() >= criteria.getMinRating()))
                .filter(p -> criteria.getMaxDetourKm() == null || p.getDetourKm() <= criteria.getMaxDetourKm())
                .skip(criteria.getOffset() == null ? 0 : criteria.getOffset())
                .limit(criteria.getLimit() == null ? places.size() : criteria.getLimit())
                .collect(Collectors.toList());
    }

    private void publishEvent(String routeId, List<Place> places) {
        Map<PlaceCategory, Integer> categoryCounts = places.stream()
                .collect(Collectors.groupingBy(Place::getCategory, Collectors.summingInt(p -> 1)));

        PlacesFetchedEvent event = PlacesFetchedEvent.builder()
                .routeId(routeId)
                .totalCount(places.size())
                .categoryCounts(categoryCounts)
                .build();

        eventProducer.publish(event);
    }
}
