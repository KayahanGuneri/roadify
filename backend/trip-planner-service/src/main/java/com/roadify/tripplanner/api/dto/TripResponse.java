package com.roadify.tripplanner.api.dto;

import java.time.Instant;
import java.util.List;

/**
 * Response payload for returning trip details with stops.
 */
public class TripResponse {

    private String id;
    private String userId;
    private String routeId;
    private String title;
    private Instant createdAt;
    private List<TripStopDto> stops;

    public TripResponse() {
    }

    public TripResponse(
            String id,
            String userId,
            String routeId,
            String title,
            Instant createdAt,
            List<TripStopDto> stops
    ) {
        this.id = id;
        this.userId = userId;
        this.routeId = routeId;
        this.title = title;
        this.createdAt = createdAt;
        this.stops = stops;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public List<TripStopDto> getStops() {
        return stops;
    }

    public void setStops(List<TripStopDto> stops) {
        this.stops = stops;
    }
}
