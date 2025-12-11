package com.roadify.places.infrastructure.geoapify;

import com.roadify.places.application.GeoapifyClient;
import com.roadify.places.infrastructure.provider.RawPlace;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * HTTP client implementation for Geoapify Places API.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GeoapifyHttpClient implements GeoapifyClient {

    @Qualifier("geoapifyWebClient")
    private final WebClient geoapifyWebClient;

    private final GeoapifyProperties properties;

    @Override
    @CircuitBreaker(name = "geoapify")
    public List<RawPlace> fetchPlaces(String routeGeometry) {

        // TODO: decode from route geometry. For now we use a fixed point for local tests.
        double lat = extractLat(routeGeometry);
        double lon = extractLon(routeGeometry);

        log.info("[Geoapify] Fetching places for lat={}, lon={}", lat, lon);

        String filter = "circle:" + lon + "," + lat + "," + 10_000;

        GeoapifyResponse response;
        try {
            response = geoapifyWebClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("categories",
                                    "catering.restaurant," +
                                            "catering.cafe," +
                                            "accommodation.hotel," +
                                            "camping.camp_site," +
                                            "camping.caravan_site," +
                                            "service.vehicle.fuel," +
                                            "commercial.supermarket," +
                                            "commercial.shopping_mall," +
                                            "tourism.attraction")
                            .queryParam("filter", filter)
                            .queryParam("limit", 50)
                            .queryParam("lang", "en")
                            .queryParam("apiKey", properties.getApiKey())
                            .build()
                    )
                    .retrieve()
                    .bodyToMono(GeoapifyResponse.class)
                    .block();
        } catch (WebClientResponseException ex) {
            log.error("[Geoapify] HTTP error while calling API. status={}, body={}",
                    ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
            return Collections.emptyList();
        } catch (Exception ex) {
            log.error("[Geoapify] Unexpected error while calling API", ex);
            return Collections.emptyList();
        }

        if (response == null || response.getFeatures() == null) {
            log.info("[Geoapify] No features returned. response={}", response);
            return Collections.emptyList();
        }

        log.info("[Geoapify] Received {} features", response.getFeatures().size());

        return response.getFeatures()
                .stream()
                .map(GeoapifyMapper::from)
                .filter(Objects::nonNull)
                .toList();
    }

    private double extractLat(String geometry) {
        // TODO: decode from geometry; for now use fixed value for local tests
        return 36.90;
    }

    private double extractLon(String geometry) {
        // TODO: decode from geometry; for now use fixed value for local tests
        return 30.70;
    }
}
