package com.nandincube.jamjot.dto;

import java.util.ArrayList;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for encapsulating the response of retrieving timestamp annotations associated track.
 */
public record GetTimestampNotesResponse (
    @Schema(description = "The list of timestamps")
    ArrayList<TimestampNoteResponseDTO> items
) {}
