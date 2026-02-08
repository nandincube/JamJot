package com.nandincube.jamjot.dto;

import java.util.ArrayList;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for encapsulating the details of a Spotify track, including its ID, name, artists, album information, and duration.
 * This DTO is used for general track information retrieval, such as when searching for tracks or retrieving track details outside the context of a playlist.
 *
 * This is the most basic representation of a track and its main purpose is to supplement SpotifyTrackDTO. 
 * SpotifyTrackDTO is used to represent tracks in the context of a playlist, where we need to include additional information such as when the track was added to the playlist and its track number in the playlist.
 * */
public record TrackInfo(
                @Schema(example="5FGhfmC9fl7TQ23pybMUyp", description = "The Spotify ID of the track") String id,
                @Schema(example="Life Will Be", description = "The name of the track") String name,
                @Schema(description = "The album information of the track") Album album,
                @Schema(description = "The list of artists for the track") ArrayList<Artist> artists,
                @Schema(example="180000", description = "The duration of the track in milliseconds") Integer duration_ms){
        public record Album(
                        @Schema(example= "Life Will Be", description = "The name of the album") String name) {
        }

        public record Artist(
                        @Schema(example = "Cleo Sol",description = "The name of the artist") String name) {
        }
}