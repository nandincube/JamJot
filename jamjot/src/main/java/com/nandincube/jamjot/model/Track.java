package com.nandincube.jamjot.model;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.lang.NonNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


@Entity(name="track")
@Table(name="track")
public class Track {
    @Id
    @Column(name ="track_id")
    private String trackID;

    @NonNull
    private String name;

    @NonNull
    private String artists;

    @NonNull
    private Duration duration;

    @OneToMany(mappedBy = "track")
    private List<PlaylistMember> playlistMemberships;

    public Track(String trackID, String name , String artists, Duration duration){
        this.duration = duration;
        this.trackID = trackID;
        this.name = name;
        this.artists = artists;
        this.playlistMemberships = new ArrayList<>();
    }

    public Track() {
    }
    
    public String getTrackID(){
        return trackID;
    }

        
    public String getName(){
        return name;
    }


    public String getArtists(){
        return artists;
    }

    public List<PlaylistMember> getPlaylistMemberships(){
        return playlistMemberships;
    }

    public boolean addToPlaylist(PlaylistMember playlistMember){
        if (playlistMember == null) playlistMemberships = new ArrayList<>();
        return playlistMemberships.add(playlistMember);
    }
    public void setTrackID(String trackID) {
        this.trackID = trackID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArtists(String artists) {
        this.artists = artists;
    }

    public void setPlaylistMemberships(ArrayList<PlaylistMember> playlistMemberships) {
        this.playlistMemberships = playlistMemberships;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }
    
}
