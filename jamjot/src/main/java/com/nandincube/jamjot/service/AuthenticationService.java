
package com.nandincube.jamjot.service;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.nandincube.jamjot.model.User;
@Service
public class AuthenticationService {
    private final UserService userService;

    public AuthenticationService(UserService userService) {
        this.userService = userService;
    }

    public String createUserIfNotExists(Authentication userToken) {
        String id = userToken.getName();
        OAuth2User oauthUser = (OAuth2User) userToken.getPrincipal();
        String displayName = oauthUser.getAttribute("display_name");

    
        if(id == null || id.isEmpty()){
            return "ID is null or empty";
        }

        Optional<User> user = userService.findById(id);
        if(!user.isPresent()) {
            User newUser = new User();
            newUser.setUserID(id);
            newUser.setDisplayName(displayName);
            userService.save(newUser);
        }

        return "SUCCESS";
    }
}