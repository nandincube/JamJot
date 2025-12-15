package com.nandincube.jamjot.Exceptions;

public class PlaylistNotFoundException extends Exception {
    public PlaylistNotFoundException() {
        super("Sorry :( Could not find playlist!");
    }   
}
