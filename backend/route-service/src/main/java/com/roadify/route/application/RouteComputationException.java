package com.roadify.route.application;

/**
 * Exception thrown when a route could not be computed by the external routing provider.
 */
public class RouteComputationException extends RuntimeException {

    public RouteComputationException(String message) {
        super(message);
    }

    public RouteComputationException(String message, Throwable cause) {
        super(message, cause);
    }
}
