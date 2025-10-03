package com.nandincube.jamjot.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.nandincube.jamjot.model.Playlist;

public interface PlaylistRepository extends JpaRepository<Playlist, String> {
    
}