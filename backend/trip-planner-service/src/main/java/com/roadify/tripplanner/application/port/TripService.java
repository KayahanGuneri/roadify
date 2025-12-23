package com.roadify.tripplanner.application.port;

import com.roadify.tripplanner.api.dto.CreateTripRequest;
import com.roadify.tripplanner.api.dto.TripResponse;
import com.roadify.tripplanner.api.dto.UpdateTripStopsRequest;

public interface TripService {

    TripResponse createTrip(String userId, CreateTripRequest request);

    TripResponse getTrip(String userId, String tripId);

    TripResponse updateStops(String userId, String tripId, UpdateTripStopsRequest request);
}
