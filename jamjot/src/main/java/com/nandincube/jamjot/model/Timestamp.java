package com.nandincube.jamjot.model;

import java.time.Duration;

import io.micrometer.common.lang.NonNull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity(name="timestamp")
@Table(name="timestamp")
public class Timestamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="timestamp_id")
    private Long timestampID;

    @NonNull
    @Column(name = "start_time")
    private Duration start;

    @NonNull
    @Column(name = "end_time")
    private Duration end;

    @NonNull
    @Column(length=400)
    private String note;

 
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "playlist_id", referencedColumnName = "playlist_id"),
        @JoinColumn(name = "track_id", referencedColumnName = "track_id"),
        @JoinColumn(name = "track_number", referencedColumnName = "track_number")
    })
    private PlaylistMember playlistMember; //timestamp belongs to a specific track in a specific playlist

    public Timestamp(Duration start, Duration end, String note, PlaylistMember playlistMember){ 
        this.start = start;
        this.end = end;
        this.note = note;
        this.playlistMember = playlistMember;
    }

    public Timestamp() {
    }

    public Long getId() {
        return timestampID;
    }

    public Duration getStart() {
        return start;
    }

    public void setStart(Duration start) {
        this.start = start;
    }

    public Duration getEnd() {
        return end;
    }

    public void setEnd(Duration end) {
        this.end = end;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public PlaylistMember getPlaylistMember() {
        return playlistMember;
    }

    public void setPlaylistMember(PlaylistMember playlistMember) {
        this.playlistMember = playlistMember;
    }

    
}