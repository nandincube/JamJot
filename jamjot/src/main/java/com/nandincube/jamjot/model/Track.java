package com.nandincube.jamjot.Model;

import java.util.ArrayList;

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

    @Column(name="image_url", nullable = true)
    private String imageURL;

    @Column(length=400)
    private String note;

    @OneToMany(mappedBy = "track")
    private ArrayList<PlaylistMember> playlists;

    public Track(String trackID, String name, String imageURL, String artists){
        this.trackID = trackID;
        this.name = name;
        this.imageURL = imageURL;
        this.note = null;
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

    public ArrayList<PlaylistMember> getPlaylists(){
        return playlists;
    }

    // public boolean addToPlaylist(PlaylistMember playlist){
    //     return playlists.add(playlist);
    // }

    public boolean addToPlaylist(Playlist playlist, Integer trackNumber){
        PlaylistMember playlistMember = new PlaylistMember(this, playlist, trackNumber);
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

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setPlaylists(ArrayList<PlaylistMember> playlists) {
        this.playlists = playlists;
    }

    
}
