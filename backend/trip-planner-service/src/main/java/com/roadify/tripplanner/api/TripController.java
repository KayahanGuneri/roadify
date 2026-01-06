package com.roadify.tripplanner.api;

import com.roadify.tripplanner.api.dto.CreateTripRequest;
import com.roadify.tripplanner.api.dto.TripResponse;
import com.roadify.tripplanner.api.dto.UpdateTripStopsRequest;
import com.roadify.tripplanner.application.service.TripServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/v1/trips")
public class TripController {

    private final TripServiceImpl tripServiceImpl;

    public TripController(TripServiceImpl tripServiceImpl) {
        this.tripServiceImpl = tripServiceImpl;
    }

    @PostMapping
    public ResponseEntity<TripResponse> createTrip(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody CreateTripRequest request
    ) {
        String userId = jwt.getSubject();
        TripResponse created = tripServiceImpl.createTrip(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<TripResponse> getTrip(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String tripId
    ) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(tripServiceImpl.getTrip(userId, tripId));
    }

    @PutMapping("/{tripId}/stops")
    public ResponseEntity<TripResponse> updateStops(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String tripId,
            @RequestBody UpdateTripStopsRequest request
    ) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(tripServiceImpl.updateStops(userId, tripId, request));
    }
}
