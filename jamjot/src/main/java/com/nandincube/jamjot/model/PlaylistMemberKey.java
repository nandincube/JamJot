package com.nandincube.jamjot.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class PlaylistMemberKey implements Serializable{
    @Column(name="playlist_id")
    private String playlistID;

    @Column(name="track_id")
    private String trackID;

    public PlaylistMemberKey(String playlistID, String trackID){
        this.playlistID = playlistID;
        this.trackID = trackID;
    }

    public String getPlaylistID() {
        return playlistID;
    }

    public void setPlaylistID(String playlistID) {
        this.playlistID = playlistID;
    }

    public String getTrackID() {
        return trackID;
    }

    public void setTrackID(String trackID) {
        this.trackID = trackID;
    }

    
}
