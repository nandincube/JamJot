package com.nandincube.jamjot.Repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.nandincube.jamjot.Model.Playlist;

public interface PlaylistRepository extends JpaRepository<Playlist, String> {
    
}