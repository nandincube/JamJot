package com.nandincube.jamjot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

//TODO: add foreign key annotation and define primary key 
@Entity
@Table(name="playlist_member")
public class PlayListMember {


    private Track track;

    private Playlist playlist;
    
}
