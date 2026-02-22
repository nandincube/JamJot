package com.nandincube.jamjot.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for encapsulating the note content associated with a playlist or track.
 * This DTO is used for incoming requests to create or update notes associated with playlists or tracks.
 */
public class NoteDTO {
    
    @Schema(example = "This is a sample note.", description = "The note content associated with a playlist or track")
    private String note;

    public NoteDTO() {
    }

    public NoteDTO(String note) {
        this.note = note;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
