package com.nandincube.jamjot.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for encapsulating the details of a Spotify track. This DTO contains both
 * information provided by TrackInfo and the date the track was added to the
 * playlist. It is used to represent tracks in the context of a playlist,
 * including when they were added and their associated metadata.
 * 
 * This DTO is meant to mirror the structure of an "item" in the "items" array found in the Spotify API response for retrieving tracks in a playlist.
 */
public record PlaylistTrackDTO(
        @Schema(example = "2026-01-18T20:00:07Z", description = "The date and time the track was added to the playlist") String added_at,
        @Schema(description = "Information about the track") TrackInfo item

) {

}