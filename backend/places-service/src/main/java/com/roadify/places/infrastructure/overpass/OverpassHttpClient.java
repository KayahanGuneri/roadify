package com.roadify.places.infrastructure.overpass;

import com.roadify.places.application.OverpassClient;
import com.roadify.places.infrastructure.provider.RawPlace;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class OverpassHttpClient implements OverpassClient {

    private final WebClient overpassWebClient;

    public OverpassHttpClient(
            @Qualifier("overpassWebClient") WebClient overpassWebClient
    ) {
        this.overpassWebClient = overpassWebClient;
    }

    @Override
    @CircuitBreaker(name = "overpass")
    public List<RawPlace> fetchPlaces(String routeGeometry) {
        // TODO: real Overpass call. For now, return dummy data.
        return List.of(
                RawPlace.builder()
                        .provider("Overpass")
                        .externalId("ovp-1")
                        .name("Demo Fuel Station")
                        .categoryTag("fuel_station")
                        .latitude(36.91)
                        .longitude(30.71)
                        .rating(null)
                        .build()
        );
    }
}

