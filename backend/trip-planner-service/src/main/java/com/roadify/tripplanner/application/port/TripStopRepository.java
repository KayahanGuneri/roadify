package com.roadify.tripplanner.application.port;

import com.roadify.tripplanner.domain.TripStop;

import java.util.List;

/**
 * Repository port for TripStop persistence.
 */
public interface TripStopRepository {

    /**
     * Persist multiple trip stops at once.
     */
    void saveAll(List<TripStop> stops);

    /**
     * Find all stops of a trip owned by the given user.
     *
     * @param tripId trip id
     * @param userId owner user id (JWT sub)
     * @return ordered list of trip stops
     */
    List<TripStop> findByTripIdAndUserId(String tripId, String userId);

    /**
     * Delete stops by ids for a given trip.
     *
     * @param tripId trip id
     * @param stopIds stop ids to delete
     * @return number of deleted rows
     */
    int deleteByTripIdAndIds(String tripId, List<String> stopIds);
}
