package com.roadify.places.application;

import com.roadify.places.domain.Place;
import com.roadify.places.domain.PlaceCategory;
import com.roadify.places.infrastructure.kafka.PlacesFetchedEvent;
import com.roadify.places.infrastructure.kafka.PlacesFetchedEventProducer;
import com.roadify.places.infrastructure.provider.PlaceNormalizer;
import com.roadify.places.infrastructure.provider.RawPlace;
import com.roadify.places.infrastructure.route.RouteSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlacesServiceTest {

    @Mock
    private RouteServiceClient routeServiceClient;

    @Mock
    private GeoapifyClient geoapifyClient;

    @Mock
    private OverpassClient overpassClient;

    @Mock
    private PlaceNormalizer placeNormalizer;

    @Mock
    private RedisTemplate<String, Place[]> placesRedisTemplate;

    @Mock
    private ValueOperations<String, Place[]> valueOperations;

    @Mock
    private KafkaTemplate<String, PlacesFetchedEvent> kafkaTemplate;

    @InjectMocks
    private PlacesService placesService;

    @Mock
    private PlacesFetchedEventProducer eventProducer;

    @BeforeEach
    void setUp() {
        when(placesRedisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void getPlacesForRoute_shouldReturnFromCache_whenCacheHit() {
        // given
        String routeId = "route-1";
        PlaceFilterCriteria criteria = new PlaceFilterCriteria(null, null, null, null, null);

        Place cachedPlace = Place.builder()
                .id("cached-1")
                .name("Cached Cafe")
                .category(PlaceCategory.CAFE)
                .latitude(1.0)
                .longitude(2.0)
                .detourKm(0.0)
                .build();

        when(valueOperations.get(anyString())).thenReturn(new Place[]{cachedPlace});

        // when
        List<Place> result = placesService.getPlacesForRoute(routeId, criteria);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Cached Cafe");

        verifyNoInteractions(routeServiceClient, geoapifyClient, overpassClient, placeNormalizer, kafkaTemplate);
    }

    @Test
    void getPlacesForRoute_shouldFetchAndCache_whenCacheMiss() {
        // given
        String routeId = "route-1";
        PlaceFilterCriteria criteria = new PlaceFilterCriteria(null, null, null, null, null);

        // Cache MISS
        when(valueOperations.get(anyString())).thenReturn(null);

        // Route summary
        RouteSummary routeSummary = new RouteSummary(
                routeId,
                500.0,
                360.0,
                "SOME_GEOMETRY"
        );
        when(routeServiceClient.getRouteById(routeId)).thenReturn(routeSummary);

        // Raw places from providers
        RawPlace raw1 = RawPlace.builder()
                .provider("Geoapify")
                .externalId("geo-1")
                .name("Demo Cafe")
                .categoryTag("cafe")
                .latitude(36.90)
                .longitude(30.70)
                .rating(4.5)
                .build();

        RawPlace raw2 = RawPlace.builder()
                .provider("Overpass")
                .externalId("ovp-1")
                .name("Demo Fuel Station")
                .categoryTag("fuel_station")
                .latitude(36.91)
                .longitude(30.71)
                .rating(null)
                .build();

        when(geoapifyClient.fetchPlaces(anyString()))
                .thenReturn(List.of(raw1));
        when(overpassClient.fetchPlaces(anyString()))
                .thenReturn(List.of(raw2));

        Place place1 = Place.builder()
                .id("Geoapify:geo-1")
                .name("Demo Cafe")
                .category(PlaceCategory.CAFE)
                .latitude(36.90)
                .longitude(30.70)
                .rating(4.5)
                .detourKm(0.0)
                .build();

        Place place2 = Place.builder()
                .id("Overpass:ovp-1")
                .name("Demo Fuel Station")
                .category(PlaceCategory.FUEL)
                .latitude(36.91)
                .longitude(30.71)
                .rating(null)
                .detourKm(0.0)
                .build();

        when(placeNormalizer.normalize(anyList(), anyString()))
                .thenReturn(List.of(place1, place2));

        // when
        List<Place> result = placesService.getPlacesForRoute(routeId, criteria);

        // then
        assertThat(result).hasSize(2);

        ArgumentCaptor<Place[]> cacheCaptor = ArgumentCaptor.forClass(Place[].class);
        verify(valueOperations).set(anyString(), cacheCaptor.capture());

        Place[] cachedArray = cacheCaptor.getValue();
        assertThat(cachedArray).isNotNull();
        assertThat(cachedArray).hasSize(2);

        verify(eventProducer).publish(any(PlacesFetchedEvent.class));

        verify(routeServiceClient).getRouteById(routeId);
        verify(geoapifyClient).fetchPlaces(anyString());
        verify(overpassClient).fetchPlaces(anyString());
        verify(placeNormalizer).normalize(anyList(), anyString());
    }
}
