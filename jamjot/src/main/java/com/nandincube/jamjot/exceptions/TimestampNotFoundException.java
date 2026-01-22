package com.nandincube.jamjot.exceptions;

public class TimestampNotFoundException extends Exception {
    public TimestampNotFoundException() {
        super("Sorry :( Could not find timestamp mismatch!");
    }   
}
