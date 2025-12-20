package com.roadify.tripplanner.api.dto;

import java.time.Instant;

/**
 * Stop representation in API responses/requests.
 */
public class TripStopDto {

    private String id;
    private String tripId;
    private String placeId;
    private int orderIndex;
    private Instant plannedArrivalTime;
    private Integer plannedDurationMinutes;

    public TripStopDto() {
    }

    public TripStopDto(
            String id,
            String tripId,
            String placeId,
            int orderIndex,
            Instant plannedArrivalTime,
            Integer plannedDurationMinutes
    ) {
        this.id = id;
        this.tripId = tripId;
        this.placeId = placeId;
        this.orderIndex = orderIndex;
        this.plannedArrivalTime = plannedArrivalTime;
        this.plannedDurationMinutes = plannedDurationMinutes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public Instant getPlannedArrivalTime() {
        return plannedArrivalTime;
    }

    public void setPlannedArrivalTime(Instant plannedArrivalTime) {
        this.plannedArrivalTime = plannedArrivalTime;
    }

    public Integer getPlannedDurationMinutes() {
        return plannedDurationMinutes;
    }

    public void setPlannedDurationMinutes(Integer plannedDurationMinutes) {
        this.plannedDurationMinutes = plannedDurationMinutes;
    }
}
