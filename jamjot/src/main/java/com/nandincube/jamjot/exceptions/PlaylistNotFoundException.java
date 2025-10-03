package com.nandincube.jamjot.exceptions;

public class PlaylistNotFoundException extends Exception {
    public PlaylistNotFoundException() {
        super("Sorry :( Could not find playlist!");
    }   
}
