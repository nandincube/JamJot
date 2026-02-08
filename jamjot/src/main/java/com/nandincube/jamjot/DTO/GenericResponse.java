package com.nandincube.jamjot.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record GenericResponse(
    @Schema(description = "A message indicating the result of an operation")
    String message
) {
    
}
