package com.nandincube.jamjot.model;

import java.util.ArrayList;

import org.springframework.lang.NonNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
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

    @Column(name="image_url", nullable = true)
    private String imageURL;

    @Column(length=400)
    private String note;

    @OneToMany(mappedBy = "playlist")
    private ArrayList<Playlist> playlists;

    public Track(String trackID, String name, String imageURL, String artists){
        this.trackID = trackID;
        this.name = name;
        this.imageURL = imageURL;
        this.note = null;
        this.artists = artists;
        this.playlists = new ArrayList<>();
    }
    
    public String getTrackID(){
        return trackID;
    }

        
    public String getName(){
        return name;
    }

        
    public String getNote(){
        return note;
    }

    public void setNote(String note){
        this.note = note;
    }

        
    public String getImageURL(){
        return imageURL;
    }

    public String getArtists(){
        return artists;
    }

    public ArrayList<Playlist> getPlaylists(){
        return playlists;
    }

    public boolean addToPlaylist(Playlist playlist){
        return playlists.add(playlist);
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

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setPlaylists(ArrayList<Playlist> playlists) {
        this.playlists = playlists;
    }

    
}
