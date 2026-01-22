package com.nandincube.jamjot.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;

@Entity
//@Table(name="playlist_member")
public class PlaylistMember {

    @EmbeddedId
    private PlaylistMemberID playlistMemberID;

    @ManyToOne
    @MapsId("trackID")
    @JoinColumn(name ="track_id")
    private Track track;

    @ManyToOne
    @MapsId("playlistID")
    @JoinColumn(name ="playlist_id")
    private Playlist playlist;

    @Column(length=400)
    private String note;

    @OneToMany(mappedBy = "playlistMember", cascade = CascadeType.ALL)
    private List<Timestamp> timestamps;

    public PlaylistMember(Track track, Playlist playlist, int trackNumber){
        this.playlist = playlist;
        this.track = track;
        this.note = "";
        this.playlistMemberID = new PlaylistMemberID(track.getTrackID(), playlist.getPlaylistID(), trackNumber);
    }

    public PlaylistMember() {
    }

    public PlaylistMemberID getPlaylistMemberID() {
        return playlistMemberID;
    }

    public void setPlaylistMemberID(PlaylistMemberID playlistMemberID) {
        this.playlistMemberID = playlistMemberID;
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

    public List<Timestamp> getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(List<Timestamp> timestamps) {
        this.timestamps = timestamps;
    }

}
