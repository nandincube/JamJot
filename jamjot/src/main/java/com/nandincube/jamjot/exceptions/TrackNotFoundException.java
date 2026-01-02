package com.nandincube.jamjot.exceptions;

public class TrackNotFoundException extends Exception {
    public TrackNotFoundException() {
        super("Sorry :( Could not find track or track number mismatch!");
    }   
}
