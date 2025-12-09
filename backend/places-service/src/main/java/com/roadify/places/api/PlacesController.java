package com.roadify.places.api;

import com.roadify.places.api.dto.PlaceResponseDTO;
import com.roadify.places.application.PlaceFilterCriteria;
import com.roadify.places.application.PlacesService;
import com.roadify.places.domain.Place;
import com.roadify.places.domain.PlaceCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/routes/{routeId}/places")
@RequiredArgsConstructor
public class PlacesController {

    private final PlacesService placesService;

    @GetMapping
    public List<PlaceResponseDTO> getPlaces(
            @PathVariable String routeId,
            @RequestParam(required = false) PlaceCategory category,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double maxDetourKm,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset
    ) {
        PlaceFilterCriteria criteria = PlaceFilterCriteria.builder()
                .category(category)
                .minRating(minRating)
                .maxDetourKm(maxDetourKm)
                .limit(limit)
                .offset(offset)
                .build();

        List<Place> places = placesService.getPlacesForRoute(routeId, criteria);
        return places.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
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
