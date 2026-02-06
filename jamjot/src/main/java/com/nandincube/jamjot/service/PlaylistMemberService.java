package com.nandincube.jamjot.service;

import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.nandincube.jamjot.model.PlaylistMember;
import com.nandincube.jamjot.model.PlaylistMemberID;
import com.nandincube.jamjot.repository.PlaylistMemberRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@Service
public class PlaylistMemberService {
    private final PlaylistMemberRepository playlistMemberRepository;
    private final Validator validator;

    public PlaylistMemberService(PlaylistMemberRepository playlistMemberRepository, Validator validator) {
        this.playlistMemberRepository = playlistMemberRepository;
        this.validator = validator;
    }


    public PlaylistMember save(PlaylistMember trackInPlaylist){
        String validationErrors = validatePlaylist(trackInPlaylist);
        if(validationErrors != null){
            throw new IllegalArgumentException("Playlist member contains invalid information: \n" + validationErrors);
        }
        return playlistMemberRepository.save(trackInPlaylist);
    }


    private String validatePlaylist(PlaylistMember trackInPlaylist){
        StringBuilder errors = new StringBuilder();
        Set<ConstraintViolation<PlaylistMember>> violations = validator.validate(trackInPlaylist);


        if (violations.isEmpty()) {
            return null;
        }

        for(ConstraintViolation<PlaylistMember> violation : violations){
            errors.append(violation.getMessage())
                    .append("\n");
        }

        return errors.toString();
    }

    public Optional<PlaylistMember> findById(PlaylistMemberID playlistMemberID){
        return playlistMemberRepository.findById(playlistMemberID);
    }
}
