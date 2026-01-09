package com.roadify.aiassistant.application.client;

import com.roadify.aiassistant.application.dto.RouteDetailsDTO;

/**
 * Abstraction over route-service HTTP API.
 *
 * Bu interface, ai-assistant-service'in route bilgisine erişmek için
 * kullandığı üst seviye sözleşmedir.
 */
public interface RouteServiceClient {

    /**
     * Given a route ID (UUID string), fetch route details from route-service.
     *
     * @param routeId route identifier as String (UUID format)
     * @return details of the route
     */
    RouteDetailsDTO getRouteById(String routeId);
}
