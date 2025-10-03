package com.nandincube.jamjot.model;

import java.util.ArrayList;

import org.springframework.lang.NonNull;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


@Entity
@Table(name="playlist")
public class Playlist {
    @Id
    @Column(name ="playlist_id")
    private String playlistID;

    @NonNull
    private String name;

    @Column(name="image_url", nullable = true)
    private String imageURL;

    @Column(length=300)
    private String note;

    @NonNull
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="user_id")
    private User user;

    @OneToMany(mappedBy = "track")
    private ArrayList<Track> tracks;

    public Playlist(String playlistID, String name, String imageURL, User user){
        this.playlistID = playlistID;
        this.name = name;
        this.imageURL = imageURL;
        this.note = null;
        this.user = user;
        this.tracks = new ArrayList<Track>();
    }
    
    public String getPlaylistID(){
        return playlistID;
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

    public User getUser(){
        return user;
    }

    public ArrayList<Track> getTracks(){
        return tracks;
    }

    public boolean addTrackToPlaylist(Track track){
        return tracks.add(track);
    }
}
