package com.roadify.places.infrastructure.opentripmap;

import com.roadify.places.application.OpenTripMapClient;
import com.roadify.places.infrastructure.provider.RawPlace;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class OpenTripMapHttpClient implements OpenTripMapClient {

    private final WebClient openTripMapWebClient;

    public OpenTripMapHttpClient(
            @Qualifier("openTripMapWebClient") WebClient openTripMapWebClient
    ) {
        this.openTripMapWebClient = openTripMapWebClient;
    }

    @Override
    @CircuitBreaker(name = "opentripmap")
    public List<RawPlace> fetchPlaces(String routeGeometry) {
        // TODO: real OpenTripMap call. For now, return dummy data.
        return List.of(
                RawPlace.builder()
                        .provider("OpenTripMap")
                        .externalId("otm-1")
                        .name("Demo Cafe")
                        .categoryTag("cafe")
                        .latitude(36.90)
                        .longitude(30.70)
                        .rating(4.5)
                        .build()
        );
    }
}

