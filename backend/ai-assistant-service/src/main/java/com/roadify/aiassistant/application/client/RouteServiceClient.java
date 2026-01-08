package com.roadify.aiassistant.application.client;

import com.roadify.aiassistant.application.dto.RouteDetailsDTO;

/**
 * Abstraction over route-service HTTP API.
 */
public interface RouteServiceClient {

    RouteDetailsDTO getRouteById(String routeId);
}
