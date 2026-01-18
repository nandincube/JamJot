package com.nandincube.jamjot.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class NoteRequest {
    
    @Schema(example = "This is a sample note.", description = "The note content associated with a playlist or track")
    private String note;

    public NoteRequest() {
    }

    public NoteRequest(String note) {
        this.note = note;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
