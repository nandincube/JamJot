package com.nandincube.jamjot.DTO;

import java.util.ArrayList;

public record PlaylistsResponse (
    String next,
    ArrayList<PlaylistDTO> items
) {}
