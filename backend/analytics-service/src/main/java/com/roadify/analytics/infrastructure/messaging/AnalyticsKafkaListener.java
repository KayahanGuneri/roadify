package com.roadify.analytics.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roadify.analytics.application.service.AnalyticsAggregationService;
import com.roadify.analytics.infrastructure.messaging.event.AiRecommendationAcceptedEvent;
import com.roadify.analytics.infrastructure.messaging.event.AiRecommendationRequestedEvent;
import com.roadify.analytics.infrastructure.messaging.event.PlacesFetchedEvent;
import com.roadify.analytics.infrastructure.messaging.event.RouteCreatedEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Kafka listeners for analytics-service.
 *
 * Türkçe Özet:
 * Kafka'dan gelen event'leri dinler ve AnalyticsAggregationService'e yönlendirir.
 */
@Component
public class AnalyticsKafkaListener {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsKafkaListener.class);

    private final ObjectMapper objectMapper;
    private final AnalyticsAggregationService aggregationService;

    public AnalyticsKafkaListener(ObjectMapper objectMapper,
                                  AnalyticsAggregationService aggregationService) {
        this.objectMapper = objectMapper;
        this.aggregationService = aggregationService;
    }

    @KafkaListener(
            topics = "route.created",
            groupId = "${roadify.analytics.kafka-group:roadify-analytics-service}"
    )
    public void onRouteCreated(ConsumerRecord<String, String> record) {
        try {
            RouteCreatedEvent event = objectMapper.readValue(record.value(), RouteCreatedEvent.class);
            LocalDate date = toUtcDate(event.getOccurredAt());
            aggregationService.handleRouteCreated(date);
            log.debug("Processed route.created event for date={}", date);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize route.created event: {}", record.value(), e);
        }
    }

    @KafkaListener(
            topics = "places.fetched",
            groupId = "${roadify.analytics.kafka-group:roadify-analytics-service}"
    )
    public void onPlacesFetched(ConsumerRecord<String, String> record) {
        try {
            PlacesFetchedEvent event = objectMapper.readValue(record.value(), PlacesFetchedEvent.class);
            LocalDate date = toUtcDate(event.getOccurredAt());

            if (event.getPlaces() == null || event.getPlaces().isEmpty()) {
                log.debug("places.fetched event has no places, skipping. date={}", date);
                return;
            }

            Map<String, Long> categoryCounts = event.getPlaces().stream()
                    .filter(p -> p.getCategory() != null && !p.getCategory().isBlank())
                    .collect(Collectors.groupingBy(
                            PlacesFetchedEvent.PlacePayload::getCategory,
                            Collectors.counting()
                    ));

            categoryCounts.forEach((category, count) -> {
                aggregationService.handlePlacesFetched(date, category, count.intValue());
            });

            log.debug("Processed places.fetched event for date={} categoryCounts={}", date, categoryCounts);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize places.fetched event: {}", record.value(), e);
        }
    }

    @KafkaListener(
            topics = "ai.recommendation.requested",
            groupId = "${roadify.analytics.kafka-group:roadify-analytics-service}"
    )
    public void onAiRecommendationRequested(ConsumerRecord<String, String> record) {
        try {
            AiRecommendationRequestedEvent event =
                    objectMapper.readValue(record.value(), AiRecommendationRequestedEvent.class);

            LocalDate date = toUtcDate(event.getOccurredAt());
            aggregationService.handleAiRecommendationRequested(date);
            log.debug("Processed ai.recommendation.requested event for date={}", date);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize ai.recommendation.requested event: {}", record.value(), e);
        }
    }

    @KafkaListener(
            topics = "ai.recommendation.accepted",
            groupId = "${roadify.analytics.kafka-group:roadify-analytics-service}"
    )
    public void onAiRecommendationAccepted(ConsumerRecord<String, String> record) {
        try {
            AiRecommendationAcceptedEvent event =
                    objectMapper.readValue(record.value(), AiRecommendationAcceptedEvent.class);

            LocalDate date = toUtcDate(event.getOccurredAt());
            aggregationService.handleAiRecommendationAccepted(date);
            log.debug("Processed ai.recommendation.accepted event for date={}", date);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize ai.recommendation.accepted event: {}", record.value(), e);
        }
    }

    private LocalDate toUtcDate(Instant instant) {
        if (instant == null) {
            // Event timestamp yoksa "bugün" varsayıyoruz (UTC).
            return LocalDate.now(ZoneOffset.UTC);
        }
        return instant.atZone(ZoneOffset.UTC).toLocalDate();
    }
}
