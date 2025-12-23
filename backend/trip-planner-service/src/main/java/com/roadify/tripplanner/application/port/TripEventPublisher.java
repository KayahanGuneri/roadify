package com.roadify.tripplanner.application.port;

import java.util.List;

public interface TripEventPublisher {

    void publishTripCreated(String tripId, String userId, String routeId);

    void publishTripStopsAdded(String tripId, String userId, List<String> stopIds);

    void publishTripStopsRemoved(String tripId, String userId, List<String> stopIds);
}
