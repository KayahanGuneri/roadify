package com.roadify.places.application;

import com.roadify.places.infrastructure.route.RouteSummary;

/**
 * Abstraction over the route-service.
 * Application layer depends on this interface, not on WebClient directly.
 */
public interface RouteServiceClient {

    RouteSummary getRouteById(String routeId);
}
