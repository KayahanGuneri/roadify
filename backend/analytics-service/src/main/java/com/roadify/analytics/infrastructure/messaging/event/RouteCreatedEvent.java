package com.roadify.analytics.infrastructure.messaging.event;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;

/**
 * Event payload for route.created topic.
 *
 * Türkçe Özet:
 * route.created Kafka event'inin payload'ı.
 * Analytics tarafında şu an sadece zaman bilgisini kullanıyoruz.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RouteCreatedEvent {

    @JsonAlias({"createdAt", "occurredAt", "timestamp"})
    private Instant occurredAt;

    public RouteCreatedEvent() {
        // Jackson için no-args ctor
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }
}
