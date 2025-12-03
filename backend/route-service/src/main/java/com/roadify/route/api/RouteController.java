package com.roadify.route.api;

import com.roadify.route.api.dto.RouteDTO;
import com.roadify.route.api.dto.RoutePreviewRequestDTO;
import com.roadify.route.application.RouteService;
import com.roadify.route.domain.Route;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST API for route preview and retrieval.
 */
@RestController
@RequestMapping("/v1/routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    /**
     * Preview a route between two coordinates.
     */
    @PostMapping("/preview")
    public ResponseEntity<RouteDTO> previewRoute(@RequestBody RoutePreviewRequestDTO request) {
        Route route = routeService.previewRoute(
                request.getFromLat(),
                request.getFromLng(),
                request.getToLat(),
                request.getToLng()
        );

        return ResponseEntity.ok(RouteMapper.toDto(route));
    }

    /**
     * Get an existing route by its id.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RouteDTO> getRouteById(@PathVariable String id) {
        Route route = routeService.getRouteById(id);
        return ResponseEntity.ok(RouteMapper.toDto(route));
    }
}
