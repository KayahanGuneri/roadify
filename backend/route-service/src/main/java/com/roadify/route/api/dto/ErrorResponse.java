package com.roadify.route.api.dto;

import lombok.Builder;
import lombok.Value;

/**
 * Standard error response format for the API.
 */
@Value
@Builder
public class ErrorResponse {

    String errorCode;
    String message;
    String details;
}
