package com.nandincube.jamjot.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record TimestampDTO(
    @Schema(example="1", description = "The unique ID of the timestamp")
    Long id,
    @Schema(example= "1:30", description = "The start time of the timestamp in the track")
    String start_time,
    @Schema(example="2:15", description = "The end time of the timestamp in the track")
    String end_time,
    @Schema(example="This is my favorite part!", description = "The note associated with the timestamp")
    String note
) {}


