package com.nandincube.jamjot.dto;

import java.util.ArrayList;

import io.swagger.v3.oas.annotations.media.Schema;

public record TimestampResponse (
    @Schema(description = "The list of timestamps")
    ArrayList<TimestampDTO> items
) {}
