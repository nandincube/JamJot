package com.nandincube.jamjot.model;

import java.util.ArrayList;
import java.util.List;

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

    @Column(length=300)
    private String note;

    @NonNull
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="user_id")
    private User user;

    @OneToMany(mappedBy = "playlist")
    private List<PlaylistMember> tracks;

    public Playlist(String playlistID, String name, User user){
        this.playlistID = playlistID;
        this.name = name;
        this.note = null;
        this.user = user;
        this.tracks = new ArrayList<PlaylistMember>();
    }

    public Playlist() {
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


    public User getUser(){
        return user;
    }

    public List<PlaylistMember> getTracks(){
        return tracks;
    }

    public boolean addTrack(PlaylistMember trackInPlaylist){
        return tracks.add(trackInPlaylist);
    }
}
