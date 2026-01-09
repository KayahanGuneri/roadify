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

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlacesService {

    private static final Duration BASE_CACHE_TTL = Duration.ofMinutes(10);

    private final RouteServiceClient routeServiceClient;
    private final GeoapifyClient geoapifyClient;
    private final OverpassClient overpassClient;
    private final PlaceNormalizer placeNormalizer;
    private final RedisTemplate<String, Place[]> placesRedisTemplate;
    private final ObjectProvider<PlacesFetchedEventProducer> eventProducerProvider;

    public List<Place> getPlacesForRoute(String routeId, PlaceFilterCriteria criteria) {

        // 0) Incoming criteria log (kanıt/diagnostic)
        log.info(
                "[PlacesService] Incoming criteria: routeId={}, category={}, maxDetourKm={}, limit={}, offset={}",
                routeId,
                criteria.getCategory(),
                criteria.getMaxDetourKm(),
                criteria.getLimit(),
                criteria.getOffset()
        );

        // 1) Cache sadece BASE ana liste için kullanılır
        String baseKey = buildBaseCacheKey(routeId);

        Place[] cachedBase = placesRedisTemplate.opsForValue().get(baseKey);
        if (cachedBase != null && cachedBase.length > 0) {
            log.info("[PlacesService] Cache hit(BASE). key={}, size={}", baseKey, cachedBase.length);

            List<Place> filtered = applyFilter(Arrays.asList(cachedBase), criteria);
            log.info("[PlacesService] Returning filtered from BASE cache. filteredSize={}", filtered.size());
            return filtered;
        }

        log.info("[PlacesService] Cache miss(BASE). key={}", baseKey);

        // 2) Fetch route
        RouteSummary route = routeServiceClient.getRouteById(routeId);
        String geometry = route.getGeometry();

        // 3) Fetch providers with status (provider fail -> cache zehirleme yok)
        List<RawPlace> rawPlaces = new ArrayList<>();

        ProviderFetchResult geo = fetchFromGeoapifySafelyWithStatus(geometry);
        log.info("[PlacesService] Geoapify returned {} raw places (success={})", geo.places().size(), geo.success());
        rawPlaces.addAll(geo.places());

        ProviderFetchResult over = fetchFromOverpassSafelyWithStatus(geometry);
        log.info("[PlacesService] Overpass returned {} raw places (success={})", over.places().size(), over.success());
        rawPlaces.addAll(over.places());

        log.info("[PlacesService] Total raw places from providers = {}", rawPlaces.size());

        // 4) Normalize
        List<Place> normalized = placeNormalizer.normalize(rawPlaces, geometry);
        log.info("[PlacesService] Normalized places count = {}", normalized.size());

        // 4.0) DETOUR ENRICH (NEW)
        List<com.roadify.places.infrastructure.geo.PolylineDecoder.LatLon> routePoints =
                com.roadify.places.infrastructure.geo.PolylineDecoder.decode(geometry);

        if (routePoints.isEmpty()) {
            log.warn("[PlacesService] Route geometry could not be decoded. detourKm will remain 0.0");
        } else {
            normalized = normalized.stream()
                    .map(p -> {
                        double detourKm = com.roadify.places.infrastructure.geo.DetourCalculator.computeDetourKm(
                                p.getLatitude(), p.getLongitude(), routePoints
                        );

                        // Place is @Data => has setters, ama burada immutable kopya oluşturuyoruz
                        return Place.builder()
                                .id(p.getId())
                                .name(p.getName())
                                .category(p.getCategory())
                                .latitude(p.getLatitude())
                                .longitude(p.getLongitude())
                                .rating(p.getRating())
                                .detourKm(detourKm)
                                .build();
                    })
                    .toList();
        }

        // 4.1) Detour stats (kanıt)
        logDetourStats(normalized);

        // 5) Cache write policy:
        // - Normalized boş değilse
        // - Ve tüm provider’lar başarılıysa (aksi halde 50’lik partial dataset cache’i bozmasın)
        boolean allProvidersOk = geo.success() && over.success();

        if (!normalized.isEmpty() && allProvidersOk) {
            placesRedisTemplate.opsForValue().set(
                    baseKey,
                    normalized.toArray(new Place[0]),
                    BASE_CACHE_TTL
            );
            log.info(
                    "[PlacesService] Cached BASE {} places under key={} (ttl={})",
                    normalized.size(),
                    baseKey,
                    BASE_CACHE_TTL
            );
        } else if (!allProvidersOk) {
            log.warn(
                    "[PlacesService] Skipping BASE cache write because a provider failed. geoOk={}, overOk={}",
                    geo.success(),
                    over.success()
            );
        } else {
            log.info(
                    "[PlacesService] Normalized list is empty. Skipping BASE cache write for key={}",
                    baseKey
            );
        }

        // 6) Publish event (istersen sadece allProvidersOk iken de publish edebilirsin; şimdilik aynı)
        publishEvent(routeId, normalized);

        // 7) Filter in-memory and return
        List<Place> filtered = applyFilter(normalized, criteria);
        log.info("[PlacesService] Filtered places count = {}", filtered.size());
        return filtered;
    }

    private void logDetourStats(List<Place> places) {
        if (places == null || places.isEmpty()) {
            log.info("[PlacesService] detour stats: empty list");
            return;
        }

        DoubleSummaryStatistics stats = places.stream()
                .mapToDouble(Place::getDetourKm)
                .summaryStatistics();

        long zeros = places.stream()
                .filter(p -> p.getDetourKm() == 0.0)
                .count();

        log.info(
                "[PlacesService] detour stats: count={}, zeros={}, min={}, max={}, avg={}",
                places.size(),
                zeros,
                stats.getMin(),
                stats.getMax(),
                stats.getAverage()
        );
    }

    private ProviderFetchResult fetchFromGeoapifySafelyWithStatus(String geometry) {
        try {
            List<RawPlace> result = geoapifyClient.fetchPlaces(geometry);
            return new ProviderFetchResult(true, (result != null) ? result : List.of());
        } catch (Exception e) {
            log.warn("[PlacesService] Geoapify fetch failed.", e);
            return new ProviderFetchResult(false, List.of());
        }
    }

    private ProviderFetchResult fetchFromOverpassSafelyWithStatus(String geometry) {
        try {
            List<RawPlace> result = overpassClient.fetchPlaces(geometry);
            return new ProviderFetchResult(true, (result != null) ? result : List.of());
        } catch (Exception e) {
            log.warn("[PlacesService] Overpass fetch failed.", e);
            return new ProviderFetchResult(false, List.of());
        }
    }

    private String buildBaseCacheKey(String routeId) {
        return "route:" + routeId + ":places:BASE";
    }

    /**
     * Rating filtresi kaldırıldı.
     * Şu an sadece:
     *  - category
     *  - maxDetourKm
     *  - offset/limit
     * üzerinden filtreleme yapıyoruz.
     */
    private List<Place> applyFilter(List<Place> places, PlaceFilterCriteria criteria) {
        if (places == null || places.isEmpty()) {
            return List.of();
        }

        int safeOffset = (criteria.getOffset() == null || criteria.getOffset() < 0)
                ? 0
                : criteria.getOffset();

        int safeLimit = (criteria.getLimit() == null || criteria.getLimit() <= 0)
                ? places.size()
                : criteria.getLimit();

        return places.stream()
                // Category filter
                .filter(p -> criteria.getCategory() == null || p.getCategory() == criteria.getCategory())
                // Rating filter YOK (Overpass rating=null olduğu için hepsi eleniyordu)
                // .filter(p -> criteria.getMinRating() == null ||
                //         (p.getRating() != null && p.getRating() >= criteria.getMinRating()))
                // Detour filter
                .filter(p -> criteria.getMaxDetourKm() == null || p.getDetourKm() <= criteria.getMaxDetourKm())
                // Pagination
                .skip(safeOffset)
                .limit(safeLimit)
                .toList();
    }

    private void publishEvent(String routeId, List<Place> places) {
        PlacesFetchedEventProducer producer = eventProducerProvider.getIfAvailable();
        if (producer == null) {
            log.debug("[PlacesService] PlacesFetchedEventProducer not available, skipping event publish.");
            return;
        }

        Map<PlaceCategory, Integer> categoryCounts = places.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        Place::getCategory,
                        java.util.stream.Collectors.summingInt(p -> 1)
                ));

        PlacesFetchedEvent event = PlacesFetchedEvent.builder()
                .routeId(routeId)
                .totalCount(places.size())
                .categoryCounts(categoryCounts)
                .build();

        producer.publish(event);

        log.info(
                "[PlacesService] Published PlacesFetchedEvent for routeId={}, totalCount={}",
                routeId,
                places.size()
        );
    }

    private record ProviderFetchResult(boolean success, List<RawPlace> places) {}
}
