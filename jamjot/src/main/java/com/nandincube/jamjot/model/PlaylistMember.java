package com.nandincube.jamjot.Model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Positive;
//https://www.baeldung.com/jpa-many-to-many
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

    // @MapsId("trackNumber")
    // @Column(name ="track_number", nullable = false)
    // @Positive
    // private Integer trackNumber;

    @Column(length=400)
    private String note;

    public PlaylistMember(Track track, Playlist playlist, int trackNumber){
        this.playlist = playlist;
        this.track = track;
        // this.trackNumber = trackNumber;
        this.note = null;
        this.playlistMemberID = new PlaylistMemberID(track.getTrackID(), playlist.getPlaylistID(), trackNumber);
    }

    public PlaylistMember() {
    }

    public PlaylistMemberID getPlaylistMemberID() {
        return playlistMemberID;
    }

    public void setPlaylistMemberId(PlaylistMemberID playlistMemberID) {
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

    // public Integer getTrackNumber() {
    //     return trackNumber;
    // }

    // public void setTrackNumber(Integer trackNumber) {
    //     this.trackNumber = trackNumber;
    // }  
}
