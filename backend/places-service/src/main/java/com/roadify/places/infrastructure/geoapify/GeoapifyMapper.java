package com.roadify.places.infrastructure.geoapify;

import com.roadify.places.infrastructure.provider.RawPlace;

import java.util.List;

/**
 * Mapper from GeoapifyResponse.Feature to internal RawPlace model.
 */
public final class GeoapifyMapper {

    private GeoapifyMapper() {
    }

    public static RawPlace from(GeoapifyResponse.Feature f) {
        GeoapifyResponse.Properties props = f.getProperties();
        GeoapifyResponse.Geometry geom = f.getGeometry();

        if (props == null || geom == null || geom.getCoordinates() == null) {
            return null;
        }

        List<Double> coords = geom.getCoordinates();
        if (coords.size() < 2) {
            return null;
        }

        double lon = coords.get(0);
        double lat = coords.get(1);

        String categoryTag = deriveCategoryTag(props);

        return RawPlace.builder()
                .provider("Geoapify")
                .externalId(props.getPlaceId())
                .name(props.getName())
                .categoryTag(categoryTag)
                .longitude(lon)
                .latitude(lat)
                .rating(props.getRating())
                .build();
    }

    /**
     * Convert Geoapify categories (e.g. "catering.cafe", "service.fuel")
     * into the simpler categoryTag strings used by SimplePlaceNormalizer.
     */
    private static String deriveCategoryTag(GeoapifyResponse.Properties props) {
        List<String> categories = props.getCategories();
        if (categories == null || categories.isEmpty()) {
            return null;
        }

        String raw = categories.get(0);
        String lower = raw.toLowerCase();

        // Map to existing tags used by SimplePlaceNormalizer tests.
        if (lower.contains("cafe") || lower.contains("coffee")) {
            return "cafe";
        }
        if (lower.contains("restaurant") || lower.contains("food")) {
            return "food";
        }
        if (lower.contains("fuel") || lower.contains("gas") || lower.contains("petrol")) {
            return "fuel_station";
        }
        if (lower.contains("hotel")) {
            return "hotel";
        }
        if (lower.contains("camp_site") || lower.contains("caravan_site") || lower.contains("camping")) {
            return "camping";
        }
        if (lower.contains("market") || lower.contains("supermarket") || lower.contains("grocery")) {
            return "market";
        }
        if (lower.contains("shop") || lower.contains("mall")) {
            return "shop";
        }
        if (lower.contains("toilet") || lower.contains("wc") || lower.contains("restroom")) {
            return "wc";
        }
        if (lower.contains("tourism") || lower.contains("attraction") || lower.contains("sightseeing")) {
            return "tourist";
        }

        // Fallback: return raw category string.
        return raw;
    }
}
