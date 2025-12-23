package com.roadify.tripplanner.api;

import com.roadify.tripplanner.api.dto.CreateTripRequest;
import com.roadify.tripplanner.api.dto.TripResponse;
import com.roadify.tripplanner.api.dto.UpdateTripStopsRequest;
import com.roadify.tripplanner.application.service.TripServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/trips")
public class TripController {

    private final TripServiceImpl tripServiceImpl;

    public TripController(TripServiceImpl tripServiceImpl) {
        this.tripServiceImpl = tripServiceImpl;
    }

    // 1) Create Trip
    @PostMapping
    public ResponseEntity<TripResponse> createTrip(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody CreateTripRequest request
    ) {
        TripResponse created = tripServiceImpl.createTrip(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 2) Get Trip by id
    @GetMapping("/{tripId}")
    public ResponseEntity<TripResponse> getTrip(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String tripId
    ) {
        return ResponseEntity.ok(tripServiceImpl.getTrip(userId, tripId));
    }

    // 3) Update stops
    @PutMapping("/{tripId}/stops")
    public ResponseEntity<TripResponse> updateStops(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String tripId,
            @RequestBody UpdateTripStopsRequest request
    ) {
        return ResponseEntity.ok(tripServiceImpl.updateStops(userId, tripId, request));
    }
}
