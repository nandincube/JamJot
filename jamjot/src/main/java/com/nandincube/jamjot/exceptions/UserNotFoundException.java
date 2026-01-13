package com.nandincube.jamjot.exceptions;

public class UserNotFoundException extends Exception {
    public UserNotFoundException() {
        super("Sorry :( Could not find user!");
    }   
}
