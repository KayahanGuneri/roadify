package com.roadify.route.api;

import com.roadify.route.api.dto.RouteDTO;
import com.roadify.route.api.dto.RoutePreviewRequestDTO;
import com.roadify.route.application.RouteService;
import com.roadify.route.domain.Route;
// import jakarta.validation.Valid;  <-- BUNU SİL

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

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

    @GetMapping("/{id}")
    public ResponseEntity<RouteDTO> getRouteById(@PathVariable String id) {
        Route route = routeService.getRouteById(id);
        return ResponseEntity.ok(RouteMapper.toDto(route));
    }
}
