package com.nandincube.jamjot.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.lang.NonNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


@Entity
@Table(name="track")
public class Track {
    @Id
    @Column(name ="track_id")
    private String trackID;

    @NonNull
    private String name;

    @NonNull
    private String artists;

    @OneToMany(mappedBy = "track")
    private List<PlaylistMember> playlists;

    public Track(String trackID, String name , String artists){
        
        this.trackID = trackID;
        this.name = name;
        this.artists = artists;
        this.playlists = new ArrayList<>();
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

    public List<PlaylistMember> getPlaylists(){
        return playlists;
    }

    public boolean addToPlaylist(PlaylistMember playlistMember){
        
        return playlists.add(playlistMember);
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

    public void setPlaylists(ArrayList<PlaylistMember> playlists) {
        this.playlists = playlists;
    }
    
}
