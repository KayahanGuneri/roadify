package com.roadify.places.infrastructure.geoapify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * DTO for Geoapify Places API response (GeoJSON FeatureCollection style).
 * Fields are intentionally minimal and defensive.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeoapifyResponse {

    private String type;
    private List<Feature> features;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Feature {

        private Properties properties;
        private Geometry geometry;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Properties {

        /**
         * Unique place identifier from Geoapify.
         */
        @JsonProperty("place_id")
        private String placeId;

        private String name;

        /**
         * List of category strings, e.g. "catering.cafe", "service.fuel".
         */
        private List<String> categories;

        /**
         * Optional rating / popularity fields.
         * Adjust the name according to the real API if needed.
         */
        private Double rating;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Geometry {

        private String type;
        /**
         * [lon, lat]
         */
        private List<Double> coordinates;
    }
}
