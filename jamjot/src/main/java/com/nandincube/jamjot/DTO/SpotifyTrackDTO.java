package com.nandincube.jamjot.dto;

import java.util.ArrayList;


public record SpotifyTrackDTO(
    String added_at,
    TrackInfo track
) {

    public record TrackInfo(
        String id,
        String name,
        Album album,
        ArrayList<Artist> artists
    ){}
    public record Album(
        String name
    ){}
    public record Artist(
        String name
    ){}
}