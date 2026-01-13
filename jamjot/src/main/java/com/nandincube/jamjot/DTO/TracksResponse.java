package com.nandincube.jamjot.dto;

import java.util.ArrayList;

public record TracksResponse (
    String next,
    ArrayList<SpotifyTrackDTO> items
) {}
