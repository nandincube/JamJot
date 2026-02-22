package com.nandincube.jamjot.dto;

import java.util.ArrayList;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for encapsulating the response of retrieving tracks from Spotify.
 * This DTO mirrors the top-level "items" array (array of PlaylistTrackObject)
 * in the Spotify API response for retrieving tracks in a playlist, which
 * includes the list of tracks and the URL for the next page of tracks if there
 * are more tracks to retrieve.
 */
public record GetTracksResponse(
        @Schema(description = "The URL for the next page of tracks") String next,
        @Schema(description = "The list of tracks") ArrayList<PlaylistTrackDTO> items) {
}
