package com.nandincube.jamjot.dto;

import java.util.ArrayList;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for encapsulating the response of retrieving tracks from Spotify.
 */
public record GetTracksResponse (
    @Schema(description = "The URL for the next page of tracks")
    String next,
    @Schema(description = "The list of tracks")
    ArrayList<SpotifyTrackDTO> items
) {}
