package com.nandincube.jamjot.exceptions;

public class UserNotFoundException extends Exception {
    public UserNotFoundException() {
        super("Error: Could not find user!");
    }   
}
