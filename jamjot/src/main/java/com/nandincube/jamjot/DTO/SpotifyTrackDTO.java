package com.nandincube.jamjot.dto;

import io.swagger.v3.oas.annotations.media.Schema;


public record SpotifyTrackDTO(
    @Schema(example="2026-01-18T20:00:07Z", description = "The date and time the track was added to the playlist")
    String added_at,
    @Schema(description = "Information about the track")
    TrackInfo track
) {


   
}