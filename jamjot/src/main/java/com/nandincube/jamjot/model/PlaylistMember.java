package com.nandincube.jamjot.model;

import io.micrometer.common.lang.NonNull;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.validation.constraints.Positive;

@Entity(name="Playlist_Member")
public class PlaylistMember {

    @EmbeddedId
    private PlaylistMemberKey playlistMemberId;

    @ManyToOne
    @MapsId("trackID")
    @JoinColumn(name ="track_id")
    private Track track;

    @ManyToOne
    @MapsId("playlistID")
    @JoinColumn(name ="playlist_id")
    private Playlist playlist;

    private String note;

    @Column(name ="track_number", nullable = false)
    @Positive
    private Integer trackNumber;

    public PlaylistMember(Track track, Playlist playlist, int trackNumber){
        this.playlist = playlist;
        this.track = track;
        this.note = null;
        this.trackNumber = trackNumber;
    }

    public PlaylistMemberKey getPlaylistMemberId() {
        return playlistMemberId;
    }

    public void setPlaylistMemberId(PlaylistMemberKey playlistMemberId) {
        this.playlistMemberId = playlistMemberId;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(Integer trackNumber) {
        this.trackNumber = trackNumber;
    }
    
}
