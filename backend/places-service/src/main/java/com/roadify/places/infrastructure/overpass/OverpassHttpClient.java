package com.roadify.places.infrastructure.overpass;

import com.roadify.places.application.OverpassClient;
import com.roadify.places.infrastructure.provider.RawPlace;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class OverpassHttpClient implements OverpassClient {

    @Qualifier("overpassWebClient")
    private final WebClient overpassWebClient;

    @Override
    @CircuitBreaker(name = "overpass")
    public List<RawPlace> fetchPlaces(String routeGeometry) {

        String bbox = "36.85,30.65,36.95,30.80";
        String query = buildOverpassQuery(bbox);

        Map<String, Object> response;
        try {
            response = overpassWebClient
                    .post()
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData("data", query))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

        } catch (WebClientResponseException ex) {
            log.error("[Overpass] HTTP error while calling API. status={}, body={}",
                    ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
            return Collections.emptyList();
        } catch (Exception ex) {
            log.error("[Overpass] Unexpected error while calling API", ex);
            return Collections.emptyList();
        }

        if (response == null) {
            log.warn("[Overpass] Response is null");
            return Collections.emptyList();
        }

        Object elementsObj = response.get("elements");
        if (!(elementsObj instanceof List<?> elementsRaw)) {
            log.warn("[Overpass] 'elements' field is missing or not a list. responseKeys={}", response.keySet());
            return Collections.emptyList();
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> elements = (List<Map<String, Object>>) (List<?>) elementsRaw;

        return elements.stream()
                .map(this::mapToRawPlace)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * routeGeometry şu an bbox gibi kullanılıyor.
     * Örn: "36.88,30.70,36.92,30.74" formatında.
     */
    private String buildOverpassQuery(String bbox) {
        return """
                [out:json];
                (
                  node["amenity"="fuel"](%s);
                  node["amenity"="cafe"](%s);
                  node["amenity"="restaurant"](%s);
                );
                out body;
                """.formatted(bbox, bbox, bbox);
    }

    private RawPlace mapToRawPlace(Map<String, Object> element) {

        @SuppressWarnings("unchecked")
        Map<String, Object> tags = (Map<String, Object>) element.get("tags");

        String name = "Unknown";
        String category = "unknown";

        if (tags != null) {
            Object nameObj = tags.get("name");
            if (nameObj != null) {
                name = String.valueOf(nameObj);
            }
            Object amenityObj = tags.get("amenity");
            if (amenityObj != null) {
                category = String.valueOf(amenityObj);
            }
        }

        Object latObj = element.get("lat");
        Object lonObj = element.get("lon");

        if (!(latObj instanceof Number latNum) || !(lonObj instanceof Number lonNum)) {
            log.warn("[Overpass] Element missing numeric lat/lon: {}", element);
            return null;
        }

        double lat = latNum.doubleValue();
        double lon = lonNum.doubleValue();

        return RawPlace.builder()
                .provider("Overpass")
                .externalId(String.valueOf(element.get("id")))
                .name(name)
                .categoryTag(category)
                .latitude(lat)
                .longitude(lon)
                .rating(null)
                .build();
    }
}
