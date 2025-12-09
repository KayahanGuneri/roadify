package com.roadify.places.infrastructure.provider;

import com.roadify.places.domain.Place;
import com.roadify.places.domain.PlaceCategory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Simple implementation of PlaceNormalizer.
 * For now:
 * - Maps provider-specific category tags to PlaceCategory.
 * - Sets detourKm to 0.0 (we will refine this later using route geometry).
 */
@Component
public class SimplePlaceNormalizer implements PlaceNormalizer {

    @Override
    public List<Place> normalize(List<RawPlace> rawPlaces, String routeGeometry) {
        // TODO: in future use routeGeometry to compute detourKm
        return rawPlaces.stream()
                .map(raw -> Place.builder()
                        .id(buildId(raw))
                        .name(raw.getName())
                        .category(mapCategory(raw.getCategoryTag()))
                        .latitude(raw.getLatitude())
                        .longitude(raw.getLongitude())
                        .rating(raw.getRating())
                        .detourKm(0.0) // for now, we will calculate later
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public PlaceCategory mapCategory(String providerCategory) {
        if (providerCategory == null) {
            return PlaceCategory.OTHER;
        }

        String tag = providerCategory.toLowerCase(Locale.ROOT);

        if (tag.contains("restaurant") || tag.contains("food")) {
            return PlaceCategory.FOOD;
        }
        if (tag.contains("cafe") || tag.contains("coffee")) {
            return PlaceCategory.CAFE;
        }
        if (tag.contains("fuel") || tag.contains("gas") || tag.contains("petrol") || tag.contains("fuel_station")) {
            return PlaceCategory.FUEL;
        }
        if (tag.contains("hotel") || tag.contains("motel")) {
            return PlaceCategory.HOTEL;
        }
        if (tag.contains("camp") || tag.contains("camping")) {
            return PlaceCategory.CAMPING;
        }
        if (tag.contains("market") || tag.contains("supermarket") || tag.contains("shop")) {
            return PlaceCategory.MARKET;
        }
        if (tag.contains("wc") || tag.contains("toilet") || tag.contains("restroom")) {
            return PlaceCategory.WC;
        }
        if (tag.contains("tourism") || tag.contains("attraction") || tag.contains("museum") || tag.contains("viewpoint")) {
            return PlaceCategory.TOURIST;
        }

        return PlaceCategory.OTHER;
    }

    private String buildId(RawPlace raw) {
        // Example: OpenTripMap:xxx, Overpass:yyy
        return raw.getProvider() + ":" + raw.getExternalId();
    }
}
