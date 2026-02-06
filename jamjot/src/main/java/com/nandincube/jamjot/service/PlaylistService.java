package com.nandincube.jamjot.service;

import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.nandincube.jamjot.model.Playlist;
import com.nandincube.jamjot.repository.PlaylistRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@Service
public class PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final Validator validator;

    public PlaylistService(PlaylistRepository playlistRepository, Validator validator) {
        this.playlistRepository = playlistRepository;
        this.validator = validator;
    }


    public Playlist save(Playlist playlist){
        String validationErrors = validatePlaylist(playlist);
        if(validationErrors != null){
            throw new IllegalArgumentException("Playlist contains invalid information: \n" + validationErrors);
        }
        return playlistRepository.save(playlist);
    }


    private String validatePlaylist(Playlist playlist){
        StringBuilder errors = new StringBuilder();
        Set<ConstraintViolation<Playlist>> violations = validator.validate(playlist);


        if (violations.isEmpty()) {
            return null;
        }

        for(ConstraintViolation<Playlist> violation : violations){
            errors.append(violation.getMessage())
                    .append("\n");
        }

        return errors.toString();
    }

    
    public Optional<Playlist> findById(String id){
        return playlistRepository.findById(id);
    }
}
