package com.roadify.route.application;

/**
 * Exception thrown when a route cannot be found by its id.
 */
public class RouteNotFoundException extends RuntimeException {

    public RouteNotFoundException(String id) {
        super("Route not found with id: " + id);
    }
}
