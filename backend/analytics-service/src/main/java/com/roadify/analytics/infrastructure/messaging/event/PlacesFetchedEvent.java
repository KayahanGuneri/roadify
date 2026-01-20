package com.roadify.analytics.infrastructure.messaging.event;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;
import java.util.List;

/**
 * Event payload for places.fetched topic.
 *
 * Türkçe Özet:
 * places.fetched Kafka event'inin payload'ı.
 * İçinde kategori bilgisi olan mekan listesi var.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlacesFetchedEvent {

    @JsonAlias({"createdAt", "occurredAt", "timestamp"})
    private Instant occurredAt;

    @JsonAlias({"places"})
    private List<PlacePayload> places;

    public PlacesFetchedEvent() {
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }

    public List<PlacePayload> getPlaces() {
        return places;
    }

    public void setPlaces(List<PlacePayload> places) {
        this.places = places;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PlacePayload {

        @JsonAlias({"category"})
        private String category;

        public PlacePayload() {
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }
    }
}
