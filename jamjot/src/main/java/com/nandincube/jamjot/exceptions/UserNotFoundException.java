package com.nandincube.jamjot.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("Error: Could not find user or issue with user authentication (re-authentication required)!");
    }   
}
