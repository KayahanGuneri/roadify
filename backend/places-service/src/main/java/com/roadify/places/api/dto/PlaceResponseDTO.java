package com.roadify.places.api.dto;

import com.roadify.places.domain.PlaceCategory;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PlaceResponseDTO {
    String id;
    String name;
    PlaceCategory category;
    double latitude;
    double longitude;
    Double rating;
    double detourKm;
}
