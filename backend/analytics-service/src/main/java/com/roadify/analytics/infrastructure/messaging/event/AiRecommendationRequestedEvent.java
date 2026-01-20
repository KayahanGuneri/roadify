package com.roadify.analytics.infrastructure.messaging.event;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;

/**
 * Event payload for ai.recommendation.requested topic.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiRecommendationRequestedEvent {

    @JsonAlias({"createdAt", "occurredAt", "timestamp"})
    private Instant occurredAt;

    public AiRecommendationRequestedEvent() {
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }
}
