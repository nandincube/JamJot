package com.nandincube.jamjot.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record TrackDTO(
    @Schema(description = "Information about the track")
    SpotifyTrackDTO track,
    @Schema(example="14", description = "The track number in the playlist")
    Integer track_number
) {}


