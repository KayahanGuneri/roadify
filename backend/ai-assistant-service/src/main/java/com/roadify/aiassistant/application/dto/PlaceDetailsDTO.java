package com.roadify.aiassistant.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Place details as returned by places-service via gateway.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceDetailsDTO {

    private String id;
    private String name;
    private String category;   // String representation of PlaceCategory
    private double latitude;
    private double longitude;
    private Double detourKm;   // may be null
}
