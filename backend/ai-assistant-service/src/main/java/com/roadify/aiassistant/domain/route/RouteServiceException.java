package com.roadify.aiassistant.domain.route;

/**
 * Türkçe Özet:
 * route-service'e yapılan çağrılarda oluşan hataları temsil eden
 * runtime exception tipi.
 */
public class RouteServiceException extends RuntimeException {

    private final String routeId;

    public RouteServiceException(String message, String routeId, Throwable cause) {
        super(message, cause);
        this.routeId = routeId;
    }

    public RouteServiceException(String message, String routeId) {
        super(message);
        this.routeId = routeId;
    }

    public String getRouteId() {
        return routeId;
    }
}
