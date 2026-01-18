package com.nandincube.jamjot.dto;

import java.util.ArrayList;

import io.swagger.v3.oas.annotations.media.Schema;

public record TrackInfo(
                @Schema(example="5FGhfmC9fl7TQ23pybMUyp", description = "The Spotify ID of the track") String id,
                @Schema(example="Life Will Be", description = "The name of the track") String name,
                @Schema(description = "The album information of the track") Album album,
                @Schema(description = "The list of artists for the track") ArrayList<Artist> artists) {
        public record Album(
                        @Schema(example= "Life Will Be", description = "The name of the album") String name) {
        }

        public record Artist(
                        @Schema(example = "Cleo Sol",description = "The name of the artist") String name) {
        }
}