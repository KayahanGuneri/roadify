package com.roadify.tripplanner.application.port;

import com.roadify.tripplanner.domain.Trip;

import java.util.Optional;

/**
 * Repository port for Trip persistence.
 * Technology-agnostic (no JDBC, no JPA).
 */
public interface TripRepository {

    /**
     * Persist a new Trip.
     */
    void save(Trip trip);

    /**
     * Find a trip by id and userId (ownership enforced).
     *
     * @param id trip id
     * @param userId owner user id (JWT sub)
     * @return optional trip
     */
    Optional<Trip> findByIdAndUserId(String id, String userId);
}
