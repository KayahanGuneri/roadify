package com.roadify.tripplanner.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain model representing a stop inside a trip.
 * Framework-free. Immutable.
 */
public final class TripStop {

    private final String id;
    private final String tripId;
    private final String placeId;
    private final String placeName; // NEW
    private final int orderIndex;
    private final Instant plannedArrivalTime;
    private final Integer plannedDurationMinutes;

    private TripStop(
            String id,
            String tripId,
            String placeId,
            String placeName,
            int orderIndex,
            Instant plannedArrivalTime,
            Integer plannedDurationMinutes
    ) {
        this.id = requireNonBlank(id, "id");
        this.tripId = requireNonBlank(tripId, "tripId");
        this.placeId = requireNonBlank(placeId, "placeId");

        // placeName is allowed to be null/blank; UI can fallback to placeId.
        this.placeName = normalizeNullable(placeName);

        if (orderIndex < 0) {
            throw new IllegalArgumentException("orderIndex must be >= 0");
        }
        this.orderIndex = orderIndex;

        if (plannedDurationMinutes != null && plannedDurationMinutes < 0) {
            throw new IllegalArgumentException("plannedDurationMinutes must be >= 0 when provided");
        }

        this.plannedArrivalTime = plannedArrivalTime;
        this.plannedDurationMinutes = plannedDurationMinutes;
    }

    public static TripStop createNew(
            String tripId,
            String placeId,
            String placeName,
            int orderIndex,
            Instant plannedArrivalTime,
            Integer plannedDurationMinutes
    ) {
        return new TripStop(
                UUID.randomUUID().toString(),
                tripId,
                placeId,
                placeName,
                orderIndex,
                plannedArrivalTime,
                plannedDurationMinutes
        );
    }

    public static TripStop restore(
            String id,
            String tripId,
            String placeId,
            String placeName,
            int orderIndex,
            Instant plannedArrivalTime,
            Integer plannedDurationMinutes
    ) {
        return new TripStop(
                id,
                tripId,
                placeId,
                placeName,
                orderIndex,
                plannedArrivalTime,
                plannedDurationMinutes
        );
    }

    public String getId() {
        return id;
    }

    public String getTripId() {
        return tripId;
    }

    public String getPlaceId() {
        return placeId;
    }

    public Optional<String> getPlaceName() {
        return Optional.ofNullable(placeName);
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public Optional<Instant> getPlannedArrivalTime() {
        return Optional.ofNullable(plannedArrivalTime);
    }

    public Optional<Integer> getPlannedDurationMinutes() {
        return Optional.ofNullable(plannedDurationMinutes);
    }

    private static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }

    private static String normalizeNullable(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TripStop that)) return false;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TripStop{" +
                "id='" + id + '\'' +
                ", tripId='" + tripId + '\'' +
                ", placeId='" + placeId + '\'' +
                ", placeName='" + placeName + '\'' +
                ", orderIndex=" + orderIndex +
                ", plannedArrivalTime=" + plannedArrivalTime +
                ", plannedDurationMinutes=" + plannedDurationMinutes +
                '}';
    }
}
