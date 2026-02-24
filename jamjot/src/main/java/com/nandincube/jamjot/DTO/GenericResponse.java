package com.nandincube.jamjot.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * A simple DTO to encapsulate generic response messages for API endpoints.
 * This can be used to provide consistent response structures for success and error messages across the API.
 */
public record GenericResponse(
    @Schema(description = "A message indicating the result of an operation")
    String message
) {
    
}
