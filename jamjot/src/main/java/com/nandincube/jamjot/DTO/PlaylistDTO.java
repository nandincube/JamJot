package com.nandincube.jamjot.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for encapsulating the details of a Spotify playlist, including its ID, name, description, and owner information.
 */
public record PlaylistDTO(
    @Schema(example="5XYhfmC9fl7TQ23pybMUyp", description = "The Spotify ID of the playlist")
    String id,
    @Schema(example="Summer Vibes", description = "The name of the playlist")
    String name,
    @Schema(example="A collection of upbeat summer tracks", description = "The description of the playlist on Spotify")
    String description,
    Owner owner
) {

    public record Owner(
        @Schema(example="44zzrj3otio46tlhe5sit7jpapop", description = "The Spotify ID of the owner of the playlist")
        String id
    ){}
}
