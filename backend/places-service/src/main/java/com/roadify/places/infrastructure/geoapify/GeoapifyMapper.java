package com.roadify.places.infrastructure.geoapify;

import com.roadify.places.infrastructure.provider.RawPlace;

import java.util.List;

public final class GeoapifyMapper {

    private GeoapifyMapper() {}

    public static RawPlace from(GeoapifyResponse.Feature feature) {
        if (feature == null || feature.getProperties() == null || feature.getGeometry() == null) {
            return null;
        }

        GeoapifyResponse.Properties props = feature.getProperties();
        GeoapifyResponse.Geometry geom = feature.getGeometry();

        String provider = "Geoapify";
        String externalId = (props.getPlaceId() != null && !props.getPlaceId().isBlank())
                ? props.getPlaceId()
                : "unknown";

        String name = (props.getName() != null && !props.getName().isBlank())
                ? props.getName()
                : "Unknown";

        List<String> categories = props.getCategories();
        // categoryTag = categories içinden seçilecek (normalizer daha iyi seçecek)
        String categoryTag = (categories != null && !categories.isEmpty()) ? categories.get(0) : null;

        double lon = 0.0;
        double lat = 0.0;

        // GeoJSON coordinates: [lon, lat]
        if (geom.getCoordinates() != null && geom.getCoordinates().size() >= 2) {
            lon = geom.getCoordinates().get(0);
            lat = geom.getCoordinates().get(1);
        } else {
            return null; // koordinat yoksa place üretme
        }

        return RawPlace.builder()
                .provider(provider)
                .externalId(externalId)
                .name(name)
                .categoryTag(categoryTag)
                .categories(categories)
                .latitude(lat)
                .longitude(lon)
                .rating(props.getRating())
                .build();
    }
}
