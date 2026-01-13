package com.nandincube.jamjot.dto;

public record PlaylistDTO(
    String id,
    String name,
    String description,
    Owner owner
) {

    public record Owner(
        String id
    ){}
}
