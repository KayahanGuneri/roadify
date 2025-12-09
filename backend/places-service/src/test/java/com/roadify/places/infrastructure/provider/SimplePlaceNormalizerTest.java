package com.roadify.places.infrastructure.provider;

import com.roadify.places.domain.Place;
import com.roadify.places.domain.PlaceCategory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SimplePlaceNormalizerTest {

    private final SimplePlaceNormalizer normalizer = new SimplePlaceNormalizer();

    @Test
    void mapCategory_shouldMapCafeTagsToCafe() {
        PlaceCategory category = normalizer.mapCategory("cafe");
        assertThat(category).isEqualTo(PlaceCategory.CAFE);

        category = normalizer.mapCategory("Coffee Shop");
        assertThat(category).isEqualTo(PlaceCategory.CAFE);
    }

    @Test
    void mapCategory_shouldMapFuelTagsToFuel() {
        PlaceCategory category = normalizer.mapCategory("fuel_station");
        assertThat(category).isEqualTo(PlaceCategory.FUEL);

        category = normalizer.mapCategory("Gas station");
        assertThat(category).isEqualTo(PlaceCategory.FUEL);
    }

    @Test
    void normalize_shouldConvertRawPlacesToDomainPlaces() {
        RawPlace raw1 = RawPlace.builder()
                .provider("OpenTripMap")
                .externalId("otm-1")
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

        List<Place> result = normalizer.normalize(List.of(raw1, raw2), "DUMMY_GEOMETRY");

        assertThat(result).hasSize(2);

        Place place1 = result.get(0);
        assertThat(place1.getId()).isEqualTo("OpenTripMap:otm-1");
        assertThat(place1.getName()).isEqualTo("Demo Cafe");
        assertThat(place1.getCategory()).isEqualTo(PlaceCategory.CAFE);
        assertThat(place1.getLatitude()).isEqualTo(36.90);
        assertThat(place1.getLongitude()).isEqualTo(30.70);
        assertThat(place1.getRating()).isEqualTo(4.5);
        assertThat(place1.getDetourKm()).isEqualTo(0.0);

        Place place2 = result.get(1);
        assertThat(place2.getId()).isEqualTo("Overpass:ovp-1");
        assertThat(place2.getCategory()).isEqualTo(PlaceCategory.FUEL);
    }
}
