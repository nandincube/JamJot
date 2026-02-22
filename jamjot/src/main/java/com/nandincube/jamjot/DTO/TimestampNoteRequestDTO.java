package com.nandincube.jamjot.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for encapsulating the note content associated with a timestamp annotation. 
 * This DTO is used for incoming requests to create or update timestamp annotations.
 */
public class TimestampNoteRequestDTO {

    @Schema(example = "This is a sample note.", description = "The note content associated with a timestamp annotation")
    private String note;

    @Schema(example = "02:20", description = "The start time in mins and seconds (mm:ss) for the timestamp")
    private String intervalStart;

    @Schema(example = "02:40", description = "The end time in mins and seconds (mm:ss) for the timestamp")
    private String intervalEnd;

    public TimestampNoteRequestDTO() {
    }

    public TimestampNoteRequestDTO(String note, String intervalStart, String intervalEnd) {
        this.note = note;
        this.intervalStart = intervalStart;
        this.intervalEnd = intervalEnd;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getIntervalStart() {
        return intervalStart;
    }

    public void setIntervalStart(String intervalStart) {
        this.intervalStart = intervalStart;
    }

    public String getIntervalEnd() {
        return intervalEnd;
    }

    public void setIntervalEnd(String intervalEnd) {
        this.intervalEnd = intervalEnd;
    }
}
