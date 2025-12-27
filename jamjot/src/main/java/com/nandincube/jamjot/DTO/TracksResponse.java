package com.nandincube.jamjot.DTO;

import java.util.ArrayList;

public record TracksResponse (
    String next,
    ArrayList<TrackDTO> items
) {}
