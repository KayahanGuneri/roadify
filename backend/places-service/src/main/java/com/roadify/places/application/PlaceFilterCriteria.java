package com.roadify.places.application;

import com.roadify.places.domain.PlaceCategory;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PlaceFilterCriteria {
    PlaceCategory category;
    Double minRating;
    Double maxDetourKm;
    Integer limit;
    Integer offset;
}
