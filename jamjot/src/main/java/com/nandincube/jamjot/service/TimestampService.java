package com.nandincube.jamjot.service;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.nandincube.jamjot.model.Timestamp;
import com.nandincube.jamjot.repository.TimestampRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@Service
public class TimestampService {
    private final TimestampRepository timestampRepository;
    private final Validator validator;

    public TimestampService(TimestampRepository timestampRepository, Validator validator) {
        this.timestampRepository = timestampRepository;
        this.validator = validator;
    }


    /**
     * Save a timestamp object
     * @param timestamp - the timestamp to save
     * @return the saved timestamp
     */
    public Timestamp save(Timestamp timestamp){
        String validationErrors = validateTimestamp(timestamp);
        if(validationErrors != null){
            throw new IllegalArgumentException("Timestamp contains invalid information: \n" + validationErrors);
        }
        return timestampRepository.save(timestamp);
    }


    /**
     * Validate a timestamp object
     * @param timestamp the timestamp to validate
     * @return null if valid, otherwise a string containing validation errors
     */
    private String validateTimestamp(Timestamp timestamp){
        StringBuilder errors = new StringBuilder();
        Set<ConstraintViolation<Timestamp>> violations = validator.validate(timestamp);


        if (violations.isEmpty()) {
            return null;
        }

        for(ConstraintViolation<Timestamp> violation : violations){
            errors.append(violation.getMessage())
                    .append("\n");
        }

        return errors.toString();
    }

    public Optional<Timestamp> findByTimestampIDAndUserID(Long timestampID, String userID) {
        return timestampRepository.findByTimestampIDAndUserID(timestampID, userID);
    }

    public Optional<Timestamp> findByTimestampID(Long id) {
        return timestampRepository.findById(id);
    }

    public void delete(Timestamp timestamp) {
        timestampRepository.delete(timestamp);
    }

    public ArrayList<Timestamp> findByPlaylistMemberID(String userID, String playlistID, String trackID, int trackNumber) {
        return (ArrayList) timestampRepository.findByPlaylistMemberID(userID, playlistID, trackID, trackNumber);
    }
}
