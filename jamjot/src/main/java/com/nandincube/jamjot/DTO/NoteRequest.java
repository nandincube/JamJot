package com.nandincube.jamjot.dto;

public class NoteRequest {
    
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
