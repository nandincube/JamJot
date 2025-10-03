package com.nandincube.jamjot.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.nandincube.jamjot.exceptions.PlaylistNotFoundException;
import com.nandincube.jamjot.model.Playlist;
import com.nandincube.jamjot.repository.PlaylistRepository;

@Service
public class AnnotationService {
    private PlaylistRepository playlistRepository;

    public AnnotationService(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    private Playlist getPlaylistByID(String userID, String playlistID) throws PlaylistNotFoundException {
        Optional<Playlist> playlistOptional = playlistRepository.findById(playlistID);

        if (playlistOptional.isEmpty()) {
            throw new PlaylistNotFoundException();
        }

        Playlist playlist = playlistOptional.get();
        String playlistOwnerID = playlist.getUser().getUserID();

        if (!userID.equals(playlistOwnerID)) {
            throw new PlaylistNotFoundException();
        }
        return playlistOptional.get();
    }


    public String getPlaylistNote(String userID, String playlistID) throws PlaylistNotFoundException {
        Playlist playlist = getPlaylistByID(userID, playlistID);

        return playlist.getNote();
    }

    public Playlist updatePlaylistNote(String userID, String playlistID, String note) throws PlaylistNotFoundException {
        Playlist playlist = getPlaylistByID(userID, playlistID);
        playlist.setNote(note);

        return playlistRepository.save(playlist);
    }

    public Playlist deletePlaylistNote(String userID, String playlistID) throws PlaylistNotFoundException {
        
        return updatePlaylistNote(userID, playlistID, null);
    }
}
