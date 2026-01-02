package com.nandincube.jamjot.dto;

import java.util.ArrayList;

public record PlaylistsResponse (
    String next,
    ArrayList<PlaylistDTO> items
) {}
