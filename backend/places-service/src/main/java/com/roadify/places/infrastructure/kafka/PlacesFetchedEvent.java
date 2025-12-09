package com.roadify.places.infrastructure.kafka;

import com.roadify.places.domain.PlaceCategory;
import lombok.Builder;
import lombok.Value;

import java.util.Map;

@Value
@Builder
public class PlacesFetchedEvent {
    String routeId;
    int totalCount;
    Map<PlaceCategory, Integer> categoryCounts;
}
