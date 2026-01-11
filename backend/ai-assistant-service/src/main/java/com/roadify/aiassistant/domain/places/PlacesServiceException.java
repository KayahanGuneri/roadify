package com.roadify.aiassistant.domain.places;

/**
 * Türkçe Özet:
 * places-service'e yapılan çağrılarda oluşan hataları temsil eden
 * runtime exception tipi.
 */
public class PlacesServiceException extends RuntimeException {

    private final String routeId;

    public PlacesServiceException(String message, String routeId, Throwable cause) {
        super(message, cause);
        this.routeId = routeId;
    }

    public PlacesServiceException(String message, String routeId) {
        super(message);
        this.routeId = routeId;
    }

    public String getRouteId() {
        return routeId;
    }
}
