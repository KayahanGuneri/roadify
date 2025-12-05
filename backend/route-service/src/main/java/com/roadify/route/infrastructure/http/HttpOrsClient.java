package com.roadify.route.infrastructure.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roadify.route.application.OrsClient;
import com.roadify.route.application.RouteComputationException;
import com.roadify.route.domain.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.UUID;

@Component
public class HttpOrsClient implements OrsClient {

    private static final Logger log = LoggerFactory.getLogger(HttpOrsClient.class);

    private final WebClient webClient;
    private final OrsProperties properties;
    private final ObjectMapper objectMapper;

    public HttpOrsClient(WebClient.Builder webClientBuilder,
                         OrsProperties properties,
                         ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;

        String rawKey = properties.getApiKey();
        String masked = rawKey == null
                ? "null"
                : (rawKey.length() <= 4 ? "****" : "****" + rawKey.substring(rawKey.length() - 4));

        log.info("Initializing HttpOrsClient with baseUrl={} and apiKey={}",
                properties.getBaseUrl(), masked);

        this.webClient = webClientBuilder
                .baseUrl(properties.getBaseUrl()) // e.g. https://api.openrouteservice.org
                .defaultHeader("Authorization", rawKey)
                .build();
    }

    @Override
    public Route computeRoute(double fromLat,
                              double fromLng,
                              double toLat,
                              double toLng) {

        log.info("Calling ORS directions API for from=({}, {}) to=({}, {})",
                fromLat, fromLng, toLat, toLng);

        // ORS expects [lng, lat]
        OrsDirectionsRequest requestBody = new OrsDirectionsRequest(
                new double[][]{
                        {fromLng, fromLat},
                        {toLng, toLat}
                }
        );

        try {
            // 1) Request body JSON
            try {
                String reqJson = objectMapper.writeValueAsString(requestBody);
                log.debug("ORS request JSON: {}", reqJson);
            } catch (Exception ex) {
                log.warn("Failed to serialize ORS request for logging", ex);
            }

            // 2) ORS çağrısı – geometry_format=encodedpolyline
            String rawResponse = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v2/directions/driving-car")
                            .queryParam("geometry_format", "encodedpolyline")
                            .queryParam("geometry", true)
                            .build()
                    )
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.debug("Raw ORS response: {}", rawResponse);

            // 3) DTO’ya çevir
            OrsDirectionsResponse response = objectMapper.readValue(
                    rawResponse,
                    OrsDirectionsResponse.class
            );

            if (response == null || response.getRoutes() == null || response.getRoutes().isEmpty()) {
                throw new RouteComputationException("Empty ORS response");
            }

            OrsDirectionsResponse.OrsRoute firstRoute = response.getRoutes().get(0);
            OrsDirectionsResponse.OrsSummary summary = firstRoute.getSummary();

            if (summary == null) {
                throw new RouteComputationException("ORS response missing summary");
            }

            double distanceKm = summary.getDistance() / 1000.0;
            double durationMinutes = summary.getDuration() / 60.0;

            // geometry artık encoded polyline string olmalı
            String geometry = null;
            Object geometryRaw = firstRoute.getGeometry();
            if (geometryRaw instanceof String) {
                geometry = (String) geometryRaw;
            } else if (geometryRaw != null) {
                log.warn("Unexpected ORS geometry type: {} - geometry will be omitted",
                        geometryRaw.getClass().getName());
            }

            return Route.builder()
                    .id(UUID.randomUUID())
                    .fromLat(fromLat)
                    .fromLng(fromLng)
                    .toLat(toLat)
                    .toLng(toLng)
                    .distanceKm(distanceKm)
                    .durationMinutes(durationMinutes)
                    .geometry(geometry)
                    .build();

        } catch (WebClientResponseException e) {
            log.error("ORS API error. Status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RouteComputationException(
                    "ORS error: " + e.getStatusCode().value() + " - " + e.getResponseBodyAsString(),
                    e
            );
        } catch (JsonProcessingException e) {
            log.error("Failed to parse ORS response JSON", e);
            throw new RouteComputationException("Failed to parse ORS response JSON", e);
        } catch (Exception e) {
            log.error("Unexpected error calling ORS", e);
            throw new RouteComputationException("Unexpected error calling ORS", e);
        }
    }
}
