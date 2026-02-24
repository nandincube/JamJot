package com.nandincube.jamjot.service;

import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.nandincube.jamjot.model.User;
import com.nandincube.jamjot.repository.UserRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final Validator validator;

    public UserService(UserRepository userRepository, Validator validator) {
        this.userRepository = userRepository;
        this.validator = validator;
    }

    /**
     * Save a user object
     * 
     * @param user - the user to save
     * @return the saved user
     */
    public User save(User user) {
        String validationErrors = validateUser(user);
        if (validationErrors != null) {
            throw new IllegalArgumentException("User contains invalid information: \n" + validationErrors);
        }
        return userRepository.save(user);
    }

    /**
     * Validate a user object
     * 
     * @param user the user to validate
     * @return null if valid, otherwise a string containing validation errors
     */
    private String validateUser(User user) {
        StringBuilder errors = new StringBuilder();
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        if (violations.isEmpty()) {
            return null;
        }

        for (ConstraintViolation<User> violation : violations) {
            errors.append(violation.getMessage())
                    .append("\n");
        }

        return errors.toString();
    }

    public Optional<User> findById(String userID) {
        return userRepository.findById(userID);
    }
}
