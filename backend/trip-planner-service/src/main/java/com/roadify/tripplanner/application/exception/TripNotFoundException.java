package com.roadify.tripplanner.application.exception;

/**
 * Thrown when a trip does not exist or is not owned by the current user.
 */
public class TripNotFoundException extends RuntimeException {

    public TripNotFoundException(String tripId) {
        super("Trip not found: " + tripId);
    }
}
