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
    private List<String> artists;

    // @Column(name="image_url", nullable = true)
    // private String imageURL;


    @OneToMany(mappedBy = "track")
    private List<PlaylistMember> playlists;

    public Track(String trackID, String name , List<String> artists){
        this.trackID = trackID;
        this.name = name;
        // this.imageURL = imageURL;
        //this.note = null;
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

        
    // public String getNote(){
    //     return note;
    // }

    // public void setNote(String note){
    //     this.note = note;
    // }

        
    // public String getImageURL(){
    //     return imageURL;
    // }

    public List<String> getArtists(){
        return artists;
    }

    public List<PlaylistMember> getPlaylists(){
        return playlists;
    }

    // public boolean addToPlaylist(PlaylistMember playlist){
    //     return playlists.add(playlist);
    // }

    // public boolean addToPlaylist(Playlist playlist, Integer trackNumber){
    //     PlaylistMember playlistMember = new PlaylistMember(this, playlist, trackNumber);

    //     return playlists.add(playlistMember);
    // }


    public boolean addToPlaylist(PlaylistMember playlistMember){
        
        return playlists.add(playlistMember);
    }
    public void setTrackID(String trackID) {
        this.trackID = trackID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArtists(ArrayList<String> artists) {
        this.artists = artists;
    }

    // public void setImageURL(String imageURL) {
    //     this.imageURL = imageURL;
    // }

    public void setPlaylists(ArrayList<PlaylistMember> playlists) {
        this.playlists = playlists;
    }

    
}
