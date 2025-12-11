package com.roadify.places.infrastructure.route;

import com.roadify.places.application.RouteServiceClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * HTTP-based implementation of RouteServiceClient using WebClient.
 */
@Component
public class HttpRouteServiceClient implements RouteServiceClient {

    private final WebClient routeServiceWebClient;

    public HttpRouteServiceClient(
            @Qualifier("routeServiceWebClient") WebClient routeServiceWebClient
    ) {
        this.routeServiceWebClient = routeServiceWebClient;
    }

    @Override
    public RouteSummary getRouteById(String routeId) {
        // TODO: replace this stub with real HTTP call to route-service
        return routeServiceWebClient
                .get()
                .uri("/v1/routes/{id}", routeId)
                .retrieve()
                .bodyToMono(RouteSummary.class)
                .block();

    }
}
