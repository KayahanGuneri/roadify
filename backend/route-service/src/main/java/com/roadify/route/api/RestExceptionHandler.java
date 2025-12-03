package com.roadify.route.api;

import com.roadify.route.api.dto.ErrorResponse;
import com.roadify.route.application.RouteComputationException;
import com.roadify.route.application.RouteNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Centralized exception handling for the Route API.
 */
@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(RouteNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRouteNotFound(RouteNotFoundException ex) {
        ErrorResponse body = ErrorResponse.builder()
                .errorCode("ROUTE_NOT_FOUND")
                .message(ex.getMessage())
                .details(null)
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(RouteComputationException.class)
    public ResponseEntity<ErrorResponse> handleRouteComputation(RouteComputationException ex) {
        ErrorResponse body = ErrorResponse.builder()
                .errorCode("ROUTE_COMPUTATION_FAILED")
                .message(ex.getMessage())
                .details(null)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        ErrorResponse body = ErrorResponse.builder()
                .errorCode("INTERNAL_ERROR")
                .message("An unexpected error occurred")
                .details(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
