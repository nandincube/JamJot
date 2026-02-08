package com.nandincube.jamjot.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for encapsulating the details of a Spotify track, including its ID, name, artists, album information, and track number in the playlist.
 * This includes all data from the SpotifyTrackDTO as well as the track number in the playlist, which is relevant when retrieving tracks in the context of a playlist.
 * Track number is included to account for the fact that the same track can appear multiple times in a playlist, where each each iteration in future may have a different note associated with it.
 */
public record TrackDTO(
    @Schema(description = "Information about the track")
    SpotifyTrackDTO entry,
    @Schema(example="14", description = "The track number in the playlist")
    Integer track_number
) {}


