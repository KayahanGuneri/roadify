package com.roadify.route.infrastructure.http;

import com.roadify.route.application.OrsClient;
import com.roadify.route.domain.Route;
import com.roadify.route.application.RouteComputationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class HttpOrsClient implements OrsClient {

    private static final Logger log = LoggerFactory.getLogger(HttpOrsClient.class);

    private final WebClient webClient;
    private final OrsProperties properties;

    public HttpOrsClient(WebClient.Builder webClientBuilder, OrsProperties properties) {
        this.webClient = webClientBuilder
                .baseUrl(properties.getBaseUrl())
                .defaultHeader("Authorization", properties.getApiKey())
                .build();
        this.properties = properties;
    }

    @Override
    public Route computeRoute(double fromLat,
                              double fromLng,
                              double toLat,
                              double toLng) {

        log.info("Calling ORS directions API for from=({}, {}) to=({}, {})",
                fromLat, fromLng, toLat, toLng);

        // ORS lon, lat bekler -> [lng, lat]
        OrsDirectionsRequest requestBody = new OrsDirectionsRequest(
                new double[][]{
                        {fromLng, fromLat},
                        {toLng, toLat}
                }
        );

        OrsDirectionsResponse response = webClient.post()
                .uri("/v2/directions/driving-car")
                .body(Mono.just(requestBody), OrsDirectionsRequest.class)
                .retrieve()
                .bodyToMono(OrsDirectionsResponse.class)
                .block();

        if (response == null || response.getRoutes() == null || response.getRoutes().isEmpty()) {
            throw new RouteComputationException("Empty ORS response");
        }

        OrsDirectionsResponse.OrsRoute firstRoute = response.getRoutes().get(0);
        OrsDirectionsResponse.OrsSummary summary = firstRoute.getSummary();

        double distanceKm = summary.getDistance() / 1000.0;    // metres -> km
        double durationMinutes = summary.getDuration() / 60.0; // seconds -> minutes

        return Route.builder()
                .id(UUID.randomUUID())
                .fromLat(fromLat)
                .fromLng(fromLng)
                .toLat(toLat)
                .toLng(toLng)
                .distanceKm(distanceKm)
                .durationMinutes(durationMinutes)
                .geometry(firstRoute.getGeometry())
                .build();
    }
}
