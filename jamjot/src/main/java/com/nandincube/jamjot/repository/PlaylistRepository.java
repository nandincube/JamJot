package com.nandincube.jamjot.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nandincube.jamjot.model.Playlist;

public interface PlaylistRepository extends JpaRepository<Playlist, String> {
        Optional<Playlist> findByPlaylistIDAndUser_UserID(String playlistID, String userId);

}