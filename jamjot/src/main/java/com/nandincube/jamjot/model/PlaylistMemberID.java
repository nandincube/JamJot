package com.nandincube.jamjot.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Positive;

@Embeddable
public class PlaylistMemberID implements Serializable {

    @Column(name = "track_id")
    private String trackID;

    @Column(name = "playlist_id")
    private String playlistID;

    @Column(name = "track_number")
    @Positive
    private Integer trackNumber;

    public PlaylistMemberID(String trackID,String playlistID, Integer trackNumber) {
        this.trackID = trackID;
        this.playlistID = playlistID;
        this.trackNumber = trackNumber;
    }

    public PlaylistMemberID() {
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

    public Integer getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(Integer trackNumber) {
        this.trackNumber = trackNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        PlaylistMemberID member = (PlaylistMemberID) o;

        if (!playlistID.equals(member.playlistID))
            return false;
        if (!trackID.equals(member.trackID))
            return false;
        return trackNumber.equals(member.trackNumber);
    }

    @Override
    public int hashCode() {
        int result = playlistID != null ? playlistID.hashCode() : 0;
        result = 31 * result + (trackID != null ? trackID.hashCode() : 0);
        result = 31 * result + (trackNumber != null ? trackNumber.hashCode() : 0);
        return result;
    }

}
