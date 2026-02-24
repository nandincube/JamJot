package com.nandincube.jamjot.exceptions;

public class PlaylistNotFoundException extends Exception {
    public PlaylistNotFoundException() {
        super("Error: Could not find playlist!");
    }   
}
