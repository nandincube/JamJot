package com.nandincube.jamjot.service;

import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.nandincube.jamjot.model.Track;
import com.nandincube.jamjot.repository.TrackRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@Service
public class TrackService {
    private final TrackRepository trackRepository;
    private final Validator validator;

    public TrackService(TrackRepository trackRepository, Validator validator) {
        this.trackRepository = trackRepository;
        this.validator = validator;
    }


    /**
     * Save a track object
     * @param track - the track to save
     * @return the saved track
     */
    public Track save(Track track){
        String validationErrors = validateTrack(track);
        if(validationErrors != null){
            throw new IllegalArgumentException("Track contains invalid information: \n" + validationErrors);
        }
        return trackRepository.save(track);
    }


    /**
     * Validate a track object
     * @param track the track to validate
     * @return null if valid, otherwise a string containing validation errors
     */
    private String validateTrack(Track track){
        StringBuilder errors = new StringBuilder();
        Set<ConstraintViolation<Track>> violations = validator.validate(track);


        if (violations.isEmpty()) {
            return null;
        }

        for(ConstraintViolation<Track> violation : violations){
            errors.append(violation.getMessage())
                    .append("\n");
        }

        return errors.toString();
    }

    public Optional<Track> findById(String trackID)  {
        return trackRepository.findById(trackID);
    }
    
}
