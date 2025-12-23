package com.roadify.tripplanner.api.dto;

/**
 * Request payload for creating a new trip.
 */
public class CreateTripRequest {

    private String routeId;
    private String title;

    public CreateTripRequest() {
    }

    public CreateTripRequest(String routeId, String title) {
        this.routeId = routeId;
        this.title = title;
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
}
