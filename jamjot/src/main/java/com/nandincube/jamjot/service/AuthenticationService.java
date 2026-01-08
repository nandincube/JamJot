
package com.nandincube.jamjot.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.nandincube.jamjot.model.User;
import com.nandincube.jamjot.repository.UserRepository;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;

    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;

    }

    public String createUser(String id, String displayName) {

        if(id == null || id.isEmpty()){
            return "ID is null or empty";
        }

        Optional<User> user = userRepository.findById(id);
        if(!user.isPresent()) {
            User newUser = new User();
            newUser.setUserID(id);
            newUser.setDisplayName(displayName);
            userRepository.save(newUser);
        }
        return "SUCCESS";
    }

    public String signup() {

        return null;
    }
}