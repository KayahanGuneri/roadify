package com.roadify.tripplanner.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain model representing a user-owned trip plan.
 * Framework-free. Immutable.
 */
public final class Trip {

    private final UUID id;
    private final String userId;
    private final String routeId;
    private final String title;
    private final Instant createdAt;

    private Trip(UUID id, String userId, String routeId, String title, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.userId = requireNonBlank(userId, "userId");
        this.routeId = requireNonBlank(routeId, "routeId");
        this.title = requireNonBlank(title, "title");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
    }

    public static Trip createNew(String userId, String routeId, String title, Instant now) {
        return new Trip(UUID.randomUUID(), userId, routeId, title, now); // toString yok
    }

    public static Trip restore(UUID id, String userId, String routeId, String title, Instant createdAt) {
        return new Trip(id, userId, routeId, title, createdAt);
    }

    public UUID getId() { return id; }

    public String getUserId() {
        return userId;
    }

    public String getRouteId() {
        return routeId;
    }

    public String getTitle() {
        return title;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    private static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trip trip)) return false;
        return id.equals(trip.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Trip{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", routeId='" + routeId + '\'' +
                ", title='" + title + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
