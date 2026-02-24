package com.nandincube.jamjot.dto;

import java.util.ArrayList;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for encapsulating the response of retrieving playlists from Spotify.
 */
public record GetPlaylistsResponse (
    @Schema(description = "The URL for the next page of playlists")
    String next,
    @Schema(description = "The list of playlists")
    ArrayList<PlaylistDTO> items
) {}
