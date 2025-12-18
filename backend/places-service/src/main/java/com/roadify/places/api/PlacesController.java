package com.roadify.places.api;

import com.roadify.places.api.dto.PlaceResponseDTO;
import com.roadify.places.application.PlaceFilterCriteria;
import com.roadify.places.application.PlacesService;
import com.roadify.places.domain.Place;
import com.roadify.places.domain.PlaceCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/routes/{routeId}/places")
@RequiredArgsConstructor
public class PlacesController {

    private final PlacesService placesService;

    @GetMapping
    public List<PlaceResponseDTO> getPlaces(
            @PathVariable String routeId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double maxDetourKm,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset
    ) {
        PlaceCategory parsedCategory = parseCategory(category);

        PlaceFilterCriteria criteria = PlaceFilterCriteria.builder()
                .category(parsedCategory)
                .minRating(minRating)
                .maxDetourKm(maxDetourKm)
                .limit(limit)
                .offset(offset)
                .build();

        return placesService.getPlacesForRoute(routeId, criteria)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private PlaceCategory parseCategory(String category) {
        if (category == null || category.isBlank()) return null;
        try {
            return PlaceCategory.valueOf(category.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            // Unknown category -> treat as null (ALL)
            return null;
        }
    }

    private PlaceResponseDTO toDto(Place place) {
        return PlaceResponseDTO.builder()
                .id(place.getId())
                .name(place.getName())
                .category(place.getCategory())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .rating(place.getRating())
                .detourKm(place.getDetourKm())
                .build();
    }
}
